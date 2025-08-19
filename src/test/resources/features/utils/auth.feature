Feature: Obter token de autenticação

  Scenario: Gerar token
    * def authUrl = 'https://auth.hml.caradhras.io/oauth2/token'
    * def basicAuthHeader = 'Basic MzVyYzU4ZGZsYW02dTdkMW40MDFzaXI2MDk6MXBkZDFpOXNoYzdwaWhrcTRkcGhxbm5sNGNvZ3ZhM2x0dmhobHFlbzU0YWZjMWRndm1ocQ=='
    Given url authUrl
    And header Content-Type = 'application/x-www-form-urlencoded'
    And header Authorization = basicAuthHeader
    And request 'grant_type=client_credentials'
    When method post
    Then status 200
    * def token = response.access_token
