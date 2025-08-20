Feature: Criar recorrência Pix com autenticação

Background:
  * def DateUtils = Java.type('utils.DateUtils')
  * def dates = DateUtils.getDates()
  * def startDate = dates.startDate
  * def expirationDate = dates.expirationDate

  * call read('classpath:features/utils/auth.feature')
  * def token = token

  * url baseUrl
  * header Authorization = 'Bearer ' + token
  * header Content-Type = 'application/json'

@jornada1 @fluxoFeliz @ci
Scenario: Criar recorrência com sucesso da Jornada 1

  Given path 'recurrence-receiver'
  And request
  """
  {
    "idAccount": 18291,
    "recurrence": {
      "retryPolicy": "RETRY_3RETRIES_7DAYS",
      "periodicity": "MONTHLY",
      "idCorrelation": "936a3ce8-b720-4547-9380-122f4e703bai",
      "startDate": "#(startDate)",
      "amount": { "currency": "BRL", "value": "1.00" },
      "debitParty": {
        "nationalRegistration": "51109573200",
        "bank": { "ispb": "13370835", "accountNumber": "211112", "branchNumber": "0001" }
      },
      "documentNumber": "DOC123456",
      "documentDescription": "Monthly subscription payment",
      "expirationDate": "#(expirationDate)",
      "initiation": { "journey": "AUT1" }
    }
  }
  """
  When method post
  Then status 201
  * eval java.lang.Thread.sleep(4000)
  * match response.recurrence.status == 'SENT'

