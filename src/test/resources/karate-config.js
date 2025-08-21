function fn() {


  var config = {};

    config.baseUrl = karate.properties['baseUrl'] || 'https://pix.hml.caradhras.io/automatic/v1';

    var RunMetrics = Java.type('utils.RunMetrics');
  RunMetrics.reset();

  karate.configure('afterScenario', function(){
  var tags = karate.info.tags || new java.util.HashSet();
  // ignore utilitários/suíte
  if (tags.contains('util') || tags.contains('suite')) return;

  var name = karate.info.scenarioName;
  var RunMetrics = Java.type('utils.RunMetrics');

  if (karate.info.error) RunMetrics.addFailed(name);
  else RunMetrics.addPassed(name, 0);
  });
  
  return config;
}
