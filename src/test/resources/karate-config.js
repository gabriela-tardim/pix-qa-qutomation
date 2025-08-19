function fn() {
  var RunMetrics = Java.type('utils.RunMetrics');
  RunMetrics.reset();

  karate.configure('afterScenario', function(){
    var name = karate.info.scenarioName;
    // aqui a gente NÃO depende de durationMillis, só das listas
    if (karate.info.error) RunMetrics.addFailed(name);
    else RunMetrics.addPassed(name, 0);
  });

  return {};
}
