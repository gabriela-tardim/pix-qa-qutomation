Feature: Obter token de autenticação

Background:
  * def tokenUrlRaw  = karate.properties['AUTH_TOKEN_URL']
  * def clientIdRaw  = karate.properties['AUTH_CLIENT_ID']
  * def clientSecRaw = karate.properties['AUTH_CLIENT_SECRET']
  * if (!tokenUrlRaw || !clientIdRaw || !clientSecRaw) karate.fail('Missing auth props: AUTH_TOKEN_URL | AUTH_CLIENT_ID | AUTH_CLIENT_SECRET')

  * def tokenUrl  = tokenUrlRaw.trim()
  * def clientId  = clientIdRaw.trim()
  * def clientSec = clientSecRaw.trim()

  * def Base64 = Java.type('java.util.Base64')
  * def basic  = 'Basic ' + Base64.getEncoder().encodeToString((clientId + ':' + clientSec).getBytes('UTF-8'))

Scenario: Gerar token
  Given url tokenUrl
  And header Content-Type = 'application/x-www-form-urlencoded; charset=UTF-8'
  And header Accept = 'application/json'
  And header Authorization = basic
  And form field grant_type = 'client_credentials'
  When method post
  * if (responseStatus != 200) karate.log('AUTH DEBUG -> status:', responseStatus, 'WWW-Authenticate:', responseHeaders['WWW-Authenticate'], 'body:', response)
  Then status 200
  * def token = response.access_token
  * match token != null