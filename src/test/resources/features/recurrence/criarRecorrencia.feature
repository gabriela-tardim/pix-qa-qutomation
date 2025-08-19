Feature: Criar recorrência Pix com autenticação


@jornada1 @fluxoFeliz
Scenario: Criar recorrência válida
  * def DateUtils = Java.type('utils.dateUtils')
  * def dates = DateUtils.getDates()
  * def startDate = dates.startDate
  * def expirationDate = dates.expirationDate

  * call read('classpath:features/utils/auth.feature')
  * def token = token

  * print '🗓️ startDate:', startDate
  * print '🗓️ expirationDate:', expirationDate
  * print '🔐 token:', token

  * def baseUrl = 'https://pix.hml.caradhras.io/automatic/v1'


  Given url baseUrl
  And path 'recurrence-receiver'
  And header Authorization = 'Bearer ' + token
  And header Content-Type = 'application/json'
  And request
  """
  {
    "idAccount": 18291,
    "recurrence": {
      "retryPolicy": "RETRY_3RETRIES_7DAYS",
      "periodicity": "MONTHLY",
      "idCorrelation": "936a3ce8-b720-4547-9380-122f4e703bai",
      "startDate": "#(startDate)",
      "amount": {
        "currency": "BRL",
        "value": "1.00"
      },
      "debitParty": {
        "nationalRegistration": "51109573200",
        "bank": {
          "ispb": "13370835",
          "accountNumber": "211112",
          "branchNumber": "0001"
        }
      },
      "documentNumber": "DOC123456",
      "documentDescription": "Monthly subscription payment",
      "expirationDate": "#(expirationDate)",
      "initiation": {
        "journey": "AUT1"
      }
    }
  }
  """
  When method post
  Then status 201

  * eval java.lang.Thread.sleep(4000)
  * match response.recurrence.status == 'SENT'

