function fn() {


  var config = {};

    config.baseUrl = karate.properties['baseUrl'] || 'https://pix.hml.caradhras.io/automatic/v1';

    var RunMetrics = Java.type('utils.RunMetrics');
  RunMetrics.reset();

  karate.configure('afterScenario', function(){
    var name = karate.info.scenarioName;
    if (karate.info.error) RunMetrics.addFailed(name);
    else RunMetrics.addPassed(name, 0);
  });
  
  return config;
}
