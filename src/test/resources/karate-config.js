function fn() {
  var p = karate.properties;
  var config = {};

  config.baseUrl = p['BASE_URL'] || 'https://pix.hml.caradhras.io/automatic/v1';

  config.authA = {
    tokenUrl:     p['AUTH_TOKEN_URL']     || 'https://auth.hml.caradhras.io/oauth2/token',
    clientId:     p['AUTH_CLIENT_ID'],
    clientSecret: p['AUTH_CLIENT_SECRET']
  };

  config.authB = {
    baseUrl:   p['PIX_SECURITY_BASE_URL']  || 'https://pix-security-infos-eks.hml.caradhras.io:5000',
    tokenPath: p['PIX_SECURITY_TOKEN_PATH'] || '/v1/caradhras_token',
    companyId: p['PIX_SECURITY_COMPANY_ID'] || '211'
  };

  return config;
}
