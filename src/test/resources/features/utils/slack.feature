@ignore
Feature: Enviar notificação para o Slack

Scenario: Notificar sucesso de recorrência
  * def SlackUtils = Java.type('utils.SlackUtils')

  # Dados do fluxo (vindos do cenário anterior)
  * def startDate = karate.get('startDate') || '--'
  * def expirationDate = karate.get('expirationDate') || '--'

  # Métricas (ajuste para tuas vars/RunMetrics se já tiver; aqui ficam defaults seguros)
  * def totalCases = karate.get('totalCases') || 1
  * def totalEstimatedHuman = karate.get('totalEstimatedHuman') || '--'
  * def successTimeHuman = karate.get('successTimeHuman') || '--'
  * def failedScenarios = karate.get('failedScenarios') || []
  * def passedScenarios = karate.get('passedScenarios') || ['SENT — start: ' + startDate + ' — exp: ' + expirationDate]

  # Título por fluxo
  * def title = 'Pix Automático – Recorrência'

  * def payload = SlackUtils.buildSummaryCard(title, totalCases, totalEstimatedHuman, successTimeHuman, failedScenarios, passedScenarios)

  * def slackWebhook = karate.properties['SLACK_WEBHOOK_URL']
  * if (!slackWebhook) karate.abort()
  

  Given url slackWebhook
  And header Content-Type = 'application/json; charset=utf-8'
  And request payload
  When method post
  Then status 200

