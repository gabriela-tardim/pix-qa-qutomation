@helper
Feature: Auth helper (client_credentials | pix-security)

@get_token_client
Scenario: token via client_credentials
  * def tokenUrl     = karate.properties['AUTH_TOKEN_URL']     || config.authA.tokenUrl
  * def clientId     = karate.properties['AUTH_CLIENT_ID']     || config.authA.clientId
  * def clientSecret = karate.properties['AUTH_CLIENT_SECRET'] || config.authA.clientSecret

  * def basic = 'Basic ' + karate.toBase64(clientId + ':' + clientSecret)

  Given url tokenUrl
  And header Content-Type = 'application/x-www-form-urlencoded'
  And header Authorization = basic
  And request 'grant_type=client_credentials'
  When method POST
  Then status 200

  * def access_token = response.access_token
  * configure headers = { Authorization: 'Bearer ' + access_token }
  * def AUTH_RESULT = { access_token: '#(access_token)', token_type: 'Bearer' }

@get_token_pix
Scenario: token via pix-security
  * def base    = karate.properties['PIX_SECURITY_BASE_URL']  || config.authB.baseUrl
  * def path    = karate.properties['PIX_SECURITY_TOKEN_PATH'] || config.authB.tokenPath
  * def company = companyId || karate.properties['PIX_SECURITY_COMPANY_ID'] || config.authB.companyId

  Given url base + path + '/' + company
  When method GET
  Then status 200

  * def access_token = response.access_token ? response.access_token : response.token
  * def AUTH_RESULT = { access_token: '#(access_token)', token_type: 'Bearer' }
  * karate.set('AUTH_RESULT', AUTH_RESULT)


@public
Scenario: get token (by method)
  * def method    = method    || 'client'     # 'client' ou 'pix'
  * def setHeader = setHeader || false
  * if (method == 'client') karate.call('classpath:features/utils/auth.feature@get_token_client')
  * if (method == 'pix')    karate.call('classpath:features/utils/auth.feature@get_token_pix')

  * def token  = AUTH_RESULT.access_token
  * def bearer = 'Bearer ' + token
  * if (setHeader) configure headers = { Authorization: '#(bearer)' }

  # retorno para o chamador
  * def result = { token: '#(token)', bearer: '#(bearer)' }
