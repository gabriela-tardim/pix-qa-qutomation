Feature: Suite Pix

# use esta suite quando quiser rodar tudo na ordem desejada
# (se preferir rodar por tag direto pelo runner, também funciona)

Scenario: Executar suíte Pix
  * call read('classpath:features/recurrence/criarRecorrencia.feature')
