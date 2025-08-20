Feature: Suite Pix

# use esta suite quando quiser rodar tudo na ordem desejada
# (se preferir rodar por tag direto pelo runner, também funciona)

Scenario: Criar recorrência com sucesso da Jornada 1
  * call read('classpath:features/recurrence/criarRecorrencia.feature')
