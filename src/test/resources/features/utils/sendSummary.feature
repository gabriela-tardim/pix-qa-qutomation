Feature: Enviar resumo da execução para Slack

Scenario: Postar card de resumo
  * def RunMetrics = Java.type('utils.RunMetrics')
  * def slackUtils = Java.type('utils.SlackUtils')

  * def total = RunMetrics.getTotalExecuted()
  * def totalEstimated = RunMetrics.getTotalEstimatedHuman()
  * def successTime = RunMetrics.fmtDur(RunMetrics.getSuccessTimeMs())
  * def failed = RunMetrics.getFailed()
  * def passed = RunMetrics.getPassed()

  * def payload = slackUtils.buildSummaryCard(total, totalEstimated, successTime, failed, passed)

  * def slackWebhook = 'https://hooks.slack.com/services/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'
  Given url slackWebhook
  And header Content-Type = 'application/json; charset=utf-8'
  And request payload
  When method post
  Then status 200
