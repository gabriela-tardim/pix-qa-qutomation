Feature: Enviar notificação para o Slack

Scenario: Notificar sucesso de recorrência
  * def startDate = __arg.startDate
  * def slackUtils = Java.type('utils.slackUtils')
  * def slackMessage = slackUtils.buildSlackMessage(startDate, 'SENT')

  * def slackWebhook = 'https://hooks.slack.com/services/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'
  

  Given url slackWebhook
  And header Content-Type = 'application/json; charset=utf-8'
  And request slackMessage
  When method post
  Then status 200

