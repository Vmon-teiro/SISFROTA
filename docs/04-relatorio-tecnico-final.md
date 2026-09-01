# 📘 Relatório Técnico Final do Sistema

**Projeto:** Sistema de Gestão Náutica e Manutenção de Embarcações  
**Documento:** `docs/04-relatorio-tecnico-final.md`  
**Versão:** 1.0  
**Data:** Setembro de 2026  
**Status:** Concluído / Homologado  

---

## 📌 1. Resumo Executivo

O **Sistema de Gestão Náutica e Manutenção de Embarcações** foi concebido e implementado para atender à necessidade de centralização, segurança e rastreabilidade na gestão de frotas fluviais e marítimas. 

Desenvolvido na linguagem **Java 17 (POO)** com suporte gráfico **Java Swing**, a solução integra a arquitetura de software **MVC (Model-View-Controller)** aos padrões **DAO (Data Access Object)**, **DTO (Data Transfer Object)** e **Service**. O armazenamento relacional é provido pelo banco **MySQL 8.0** via driver JDBC.

### Principais Resultados Alcançados:
* **Conformidade Náutica:** Automação do bloqueio de embarcações inativas ou com manutenção pendente, além do controle rígido de lotação máxima e vencimento da Caderneta de Inscrição e Registro (CIR) dos tripulantes (**RN01** e **RN02**).
* **Manutenção Preventiva Preditiva:** Acompanhamento dinâmico do horímetro dos motores com alertas preditivos para revisões periódicas (margem de 50 horas) e vencimentos regulatórios (15 dias).
* **Integridade Transacional (ACID):** Garantia de consistência total nas operações da oficina mecânica, sincronizando o encerramento da Ordem de Serviço com a atualização do status da embarcação em lote.
* **Segurança da Informação:** Criptografia de senhas através de hash BCrypt com salt, prevenindo vulnerabilidades de armazenamento de credenciais.

---

## 🏗️ 2. Arquitetura da Solução e Padrões de Projeto

A arquitetura do projeto foi desenhada visando baixo acoplamento e alta coesão entre os componentes do sistema.

```text
+-----------------------------------------------------------------------+
|                             CAMADA VIEW                               |
|        (TelaLogin, TelaPrincipal - Java Swing / Event Dispatch)       |
+-----------------------------------------------------------------------+
                                   |
                                   v
+-----------------------------------------------------------------------+
|                          CAMADA CONTROLLER                            |
| (UsuarioController, EmbarcacaoController, ViagemController, etc.)     |
+-----------------------------------------------------------------------+
                        |                      |
                        v                      v
+-------------------------------+      +--------------------------------+
|        CAMADA SERVICE         |      |           CAMADA DTO           |
| (EmailService, PDFService)    |      | (ConsultaHorarioDTO, etc.)     |
+-------------------------------+      +--------------------------------+
                        |                      |
                        +-----------+----------+
                                    |
                                    v
+-----------------------------------------------------------------------+
|                           CAMADA DAO                                  |
| (UsuarioDAO, EmbarcacaoDAO, ViagemDAO, TecnicoDAO, ConexaoDAO)        |
+-----------------------------------------------------------------------+
                                   |
                                   v (JDBC / SQL Parametrizado)
+-----------------------------------------------------------------------+
|                         BANCO DE DADOS                                |
|                   (MySQL 8.0 - gestao_nautica_db)                     |
+-----------------------------------------------------------------------+
```

### Detalhamento dos Padrões Aplicados:
1. **MVC (Model-View-Controller):** Separação clara entre a lógica de apresentação (Swing), controle de fluxo e regras de negócio.
2. **DAO (Data Access Object):** Centraliza os comandos SQL em classes especializadas, isolando o código Java de queries específicas de banco de dados.
3. **DTO (Data Transfer Object):** Utilizado para transferir coleções otimizadas de dados em consultas complexas envolvendo múltiplos `JOINs` (ex: `ConsultaHorarioDTO` e `CustoEmbarcacaoDTO`).
4. **Service:** Concentra serviços utilitários de infraestrutura, como o envio de e-mails em segundo plano (`EmailService`) e geração de arquivos PDF (`PDFService`).

---

## 🔒 3. Módulos de Segurança e Controle Transacional

### 3.1 Segurança e Criptografia (BCrypt)
As senhas de usuários não são armazenadas em texto plano. No momento do cadastro/atualização, o `UsuarioDAO` aplica a biblioteca `jbcrypt`:
```java
String hashSenha = BCrypt.hashpw(senhaPura, BCrypt.gensalt(12));
```
Durante a autenticação, a verificação ocorre via `BCrypt.checkpw(senhaInformada, hashBanco)`. Todas as requisições SQL utilizam `PreparedStatement` para neutralizar ataques de **SQL Injection**.

### 3.2 Controle Transacional ACID na Oficina Mecânica
Na conclusão de manutenções em `TecnicoDAO`, o sistema executa uma transação atômica. Se a atualização da OS ou do horímetro da embarcação falhar, a transação realiza `rollback`:

```java
try {
    conn.setAutoCommit(false); // Início da transação ACID
    
    // 1. Atualiza a Ordem de Serviço
    psOS.executeUpdate();
    
    // 2. Atualiza o horímetro e status da embarcação
    psEmbarcacao.executeUpdate();
    
    conn.commit(); // Efetiva ambas as alterações
} catch (SQLException e) {
    if (conn != null) conn.rollback(); // Desfaz alterações em caso de erro
    throw e;
} finally {
    conn.setAutoCommit(true);
}
```

---

## 🗄️ 4. Modelagem e Estrutura do Banco de Dados

O banco de dados relacional `gestao_nautica_db` foi projetado na terceira forma normal (3FN).

### Principais Tabelas e Relacionamentos:

```text
[usuarios] ── (1:N) ──> [logs_acesso]
[embarcacoes] ── (1:N) ──> [viagens] <── (1:N) ── [tripulantes (comandante)]
[embarcacoes] ── (1:N) ──> [manutencoes]
[embarcacoes] ── (1:N) ──> [abastecimentos] <── (1:N) ── [fornecedores]
[embarcacoes] ── (1:N) ──> [incidentes] <── (0:1) ── [viagens]
```

### Resumo do Script DDL (`database/script.sql`):
* `usuarios` (id, nome, email, senha_hash, perfil, status)
* `embarcacoes` (id, nome, modelo, ano_fabricacao, capacidade_passageiros, capacidade_carga, horimetro_atual, status)
* `tripulantes` (id, nome, cpf, cir_matricula, categoria, vencimento_cir, telefone)
* `viagens` (id, embarcacao_id, comandante_id, destino, qtd_passageiros, data_partida, data_chegada, status)
* `manutencoes` (id, embarcacao_id, tipo, descricao, data_agendamento, data_conclusao, valor, status)
* `abastecimentos` (id, embarcacao_id, fornecedor_id, litros, valor_total, data_abastecimento)
* `incidentes` (id, embarcacao_id, viagem_id, descricao, gravidade, data_hora, status)

---

## 🧪 5. Plano de Testes e Relatório de Validação

Os testes foram categorizados em funcionais, regras de negócio e segurança.

### 5.1 Matriz de Testes Executados

| ID | Caso de Teste | Descrição / Entrada | Resultado Esperado | Status |
| :---: | :--- | :--- | :--- | :---: |
| **CT01** | Autenticação BCrypt | E-mail e senha válidos | Login efetuado e redirecionamento conforme perfil | **PASSOU** |
| **CT02** | Bloqueio de Login Inválido | Senha incorreta | Exibição de mensagem de erro e tentativa registrada | **PASSOU** |
| **CT03** | Validação RN01 (Embarcação) | Alocar embarcação `EM_MANUTENCAO` em nova viagem | Bloqueio imediato da viagem com alerta ao usuário | **PASSOU** |
| **CT04** | Validação RN02 (Dupla Alocação) | Alocar comandante ativo em viagem sobreposta | Bloqueio e notificação de indisponibilidade | **PASSOU** |
| **CT05** | Validação RN02 (Capacidade) | Inserir passageiros > capacidade da embarcação | Bloqueio com alerta de capacidade excedida | **PASSOU** |
| **CT06** | Transação ACID na Oficina | Finalizar OS e simular falha no banco | Rollback completo (OS permanece aberta e horímetro inalterado) | **PASSOU** |
| **CT07** | Alerta Preditivo CIR | Tripulante com CIR vencendo em <= 15 dias | Exibição de aviso no painel de alertas do sistema | **PASSOU** |
| **CT08** | Alerta Horímetro | Motor a <= 50h da revisão preventiva | Notificação na Dashboard do Técnico | **PASSOU** |
| **CT09** | Exportação PDF | Solicitada exportação de relatório gerencial | Arquivo PDF gerado via OpenPDF sem erros | **PASSOU** |

---

## 📈 6. Desafios Encontrados e Soluções Adotadas

1. **Gestão do Ciclo de Vida da Conexão JDBC:**
   * *Desafio:* Risco de vazamento de conexões (*connection leaks*) em consultas frequentes da interface gráfica.
   * *Solução:* Garantia do padrão `try-with-resources` em todas as chamadas de `PreparedStatement` e `ResultSet` dentro das classes DAO.

2. **Renderização de Alertas Preditivos no Swing:**
   * *Desafio:* Carregar alertas sem travar a Thread principal da interface gráfica (*Event Dispatch Thread - EDT*).
   * *Solução:* Execução assíncrona das consultas de alerta via `SwingWorker` no carregamento da `TelaPrincipal`.

3. **Conversão de Ocorrências em Manutenções Corretivas:**
   * *Desafio:* Vincular incidentes de diário de bordo diretamente com a criação de Ordens de Serviço.
   * *Solução:* Criação do método `converterIncidenteEmOS` na camada `ManutencaoController`, alterando automaticamente o status do incidente para `EM_ANALISE`.

---

## 🎯 7. Conclusão e Trabalhos Futuros

O **Sistema de Gestão Náutica e Manutenção de Embarcações** foi concluído com sucesso, atingindo 100% da cobertura dos Requisitos Funcionais e Regras de Negócio estabelecidas. A solução demonstrou alta estabilidade, segurança robusta e excelente desempenho no ambiente de testes.

### Propostas para Evoluções Futuras:
* **Módulo IoT Telemetria:** Integração com sensores de horímetro e GPS instalados nas embarcações para sincronização em tempo real via API REST.
* **Aplicativo Mobile para Tripulantes:** Desenvolvimento de app Android/iOS para registro offline de diário de bordo e check-in de passageiros via QR Code.
* **Business Intelligence (BI):** Módulo de análise preditiva de consumo de combustível e desgaste de peças baseado em histórico operacional.
