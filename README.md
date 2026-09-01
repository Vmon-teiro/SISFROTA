# ⚓ Sistema de Gestão Náutica e Manutenção de Embarcações

[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0%2B-blue.svg)](https://www.mysql.com/)
[![Architecture](https://img.shields.io/badge/Architecture-MVC%20%2B%20DAO-green.svg)]()
[![License](https://img.shields.io/badge/License-MIT-brightgreen.svg)](LICENSE)

O **Sistema de Gestão Náutica e Manutenção de Embarcações** é uma aplicação desktop desenvolvida em Java (POO), estruturada na arquitetura **MVC (Model-View-Controller)** com padrão **DAO (Data Access Object)** e integrada ao banco de dados relacional **MySQL** via JDBC.

O sistema visa centralizar o controle operacional, administrativo e técnico de frotas fluviais e marítimas, abrangendo a gestão de tripulação, diário de viagens, monitoramento de abastecimentos, controle de incidentes, além do agendamento e histórico de manutenções preventivas e corretivas com alertas automáticos de vencimento de documentos e revisões por horímetro.

---

## 📌 Sumário
- [1. Visão Geral do Sistema](#1-visão-geral-do-sistema)
- [2. Perfis de Usuário e Dashboards](#2-perfis-de-usuário-e-dashboards)
- [3. Arquitetura e Estrutura de Pastas](#3-arquitetura-e-estrutura-de-pastas)
- [4. Mapeamento Detalhado de Camadas e Classes](#4-mapeamento-detalhado-de-camadas-e-classes)
  - [4.1 Camada Controller](#41-camada-controller)
  - [4.2 Camada DAO (Data Access Object)](#42-camada-dao-data-access-object)
  - [4.3 Camada Model (Entidades)](#43-camada-model-entidades)
  - [4.4 Camada View (Interface Swing)](#44-camada-view-interface-swing)
  - [4.5 Camada DTO & Service](#45-camada-dto--service)
- [5. Regras de Negócio e Segurança](#5-regras-de-negócio-e-segurança)
- [6. Tecnologias e Bibliotecas](#6-tecnologias-e-bibliotecas)
- [7. Configuração do Banco de Dados e Instalação](#7-configuração-do-banco-de-dados-e-instalação)
- [8. Documentação Adicional](#8-documentação-adicional)
- [9. Licença](#9-licença)

---

## 1. Visão Geral do Sistema

A operação de frotas marítimas e fluviais demanda rígidos controles operacionais, de navegação e de manutenção preventiva/corretiva para garantir a segurança da navegação, o cumprimento de regulamentações marítimas e o controle de custos. 

O sistema oferece uma solução integrada que contempla:
* **Segurança e Conformidade:** Bloqueio automático de viagens para embarcações inativas/em manutenção ou tripulantes com habilitação vencida.
* **Manutenção Baseada em Horímetro:** Monitoramento de uso das embarcações com alertas para revisões periódicas.
* **Gestão Financeira e Operacional:** Acompanhamento integrado de despesas de manutenção, suprimento de combustível e diário de bordo.
* **Rastreabilidade de Incidentes:** Conversão direta de ocorrências de bordo em Ordens de Serviço (OS) corretivas.

---

## 2. Perfis de Usuário e Dashboards

O sistema possui controle de acesso baseado em papéis (*Role-Based Access Control - RBAC*), direcionando cada perfil para uma visão personalizada (Dashboard) ajustada às suas responsabilidades:

| Perfil | Escopo e Responsabilidades | Visão e Recursos da Dashboard |
| :--- | :--- | :--- |
| **👑 Administrador(a)** | Gestão macro do sistema, controle financeiro de custos, segurança e auditoria de dados | Visão geral da frota (embarcações ativas/inativas), gráfico consolidado de custos (manutenção e combustível), alertas globais de documentos a vencer, gestão de acessos e rotinas de backup. |
| **⚓ Operador(a) / Despachante** | Gestão diária das operações de navegação, diário de bordo e logística | Próximas saídas e chegadas, registro rápido de viagens, lista de tripulantes e comandantes disponíveis, registro de abastecimentos e central de incidentes pendentes. |
| **🔧 Responsável Técnico** | Gestão da integridade física, mecânica e revisões preventivas/corretivas das embarcações | Alertas de manutenções vencidas ou a vencer (<= 15 dias), alertas por horímetro de motor (<= 50 horas), gestão de Ordens de Serviço em aberto e histórico técnico de peças/motores. |

---

## 3. Arquitetura e Estrutura de Pastas

A aplicação foi desenvolvida seguindo os padrões **MVC**, **DAO**, **DTO** e **Service**:

```text
GESTAO_NAUTICA/
├── .vscode/                 # Configurações de ambiente do VS Code
│   └── settings.json
├── database/                # Scripts SQL do Banco de Dados
│   └── script.sql           # Script de DDL e DML para o MySQL ('gestao_nautica_db')
├── docs/                    # Documentação técnica e de projeto
│   ├── images/              # Capturas de tela e protótipos visuais
│   ├── 01-especificacao-requisitos.md
│   ├── 02-prototipos-interface.md
│   ├── 03-planejamento-ms-project.md
│   └── 04-relatorio-tecnico-final.md
├── lib/                     # Bibliotecas JAR (Dependências do projeto)
│   ├── activation-1.1.1.jar       # Suporte a tipos MIME para envio de e-mails
│   ├── javax.mail-1.6.2.jar       # Protocolo SMTP / JavaMail API
│   ├── jbcrypt-0.4.jar            # Hashing criptográfico de senhas (BCrypt)
│   ├── mysql-connector-j-8.3.0.jar# Driver de conexão JDBC MySQL
│   └── openpdf-1.3.30.jar         # Geração e exportação de relatórios PDF
└── src/                     # Código Fonte Java
    ├── controller/          # Lógica de controle e mediação
    ├── dao/                 # Camada de Persistência JDBC
    ├── dto/                 # Objetos de Transferência de Dados
    ├── model/               # Entidades de Domínio
    ├── service/             # Regras de Negócio e Serviços Auxiliares
    └── view/                # Interface Gráfica (Java Swing)
```

---

## 4. Mapeamento Detalhado de Camadas e Classes

### 4.1 Camada Controller
Responsável por interceptar as ações da interface gráfica, aplicar validações primárias, acionar a camada DAO/Service e retornar o resultado para a View.

* **`UsuarioController`**
  * Gerencia a autenticação e validação de credenciais de usuários.
  * `autenticar(String email, String senha)`: Valida campos obrigatórios, aplica sanitização (`trim()`) e consulta o repositório via `UsuarioDAO.autenticar()`.
* **`EmbarcacaoController`**
  * Realiza a gestão do cadastro de frotas.
  * `listarEmbarcacoes()`: Retorna todas as embarcações registradas no banco.
  * `salvarOuAtualizar(...)`: Valida presença de nome/modelo, exige ano de fabricação entre 1900 e 2030, instancia a entidade `Embarcacao` e alterna entre inclusão (`dao.salvar`) ou edição (`dao.atualizar`).
  * `excluirEmbarcacao(int id)`: Executa a exclusão de registro pelo ID.
* **`ManutencaoController`**
  * Controla o fluxo de trabalho de manutenção.
  * `obterIncidentesPendentes()` e `obterManutencoes()`: Recupera listagens consolidadas via `IncidenteDAO` e `ManutencaoDAO`.
  * `criarOrdemServico(...)`: Valida descrição/datas e registra novas Ordens de Serviço.
  * `converterIncidenteEmOS(...)`: Converte automaticamente um incidente cadastrado em uma OS Corretiva e altera o status do incidente para `EM_ANALISE`.
  * `atualizarStatusOS(...)`: Atualiza o estágio do ciclo de vida da OS.
* **`TripulanteController`**
  * Gerencia a tripulação e habilitações marítimas.
  * `listarTripulantes()`: Carrega a lista completa de tripulantes.
  * `salvarOuAtualizar(...)`: Valida Nome, CPF, CIR (Caderneta de Inscrição e Registro) e data de vencimento da habilitação antes de salvar/atualizar.
  * `excluirTripulante(int id)`: Remove o tripulante pelo ID.
* **`ViagemController`**
  * Aplica as Regras de Negócio **RN01** e **RN02** da navegação.
  * `registrarViagem(...)`: Valida disponibilidade da embarcação/comandante, verifica status ativo e bloqueia excedente de capacidade de passageiros.
  * `excluirViagem(int idViagem)`: Valida ID e solicita a remoção ao banco.
* **`RelatorioController`**
  * Consolida informações gerenciais.
  * `CustoEmbarcacaoDTO`: DTO interno com contagem de manutenções e soma total de custos.
  * `obterConsolidadoCustos()`: Consulta o agrupamento de custos de manutenções/abastecimentos por embarcação.
* **`AlertaController`**
  * `verificarAlertasVencimento()`: Executa consultas preditivas para levantar tripulantes com CIR a vencer nos próximos 15 dias e manutenções preventivas agendadas no mesmo período.
* **`AbastecimentoController`**
  * `registrar(...)`: Valida volume abastecido (litros > 0), valor total positivo e fornecedor informado, persistindo via DAO.
* **`IncidenteController`**
  * `registrarIncidente(...)`: Valida dados da ocorrência, vincula opcionalmente à viagem e gera o registro no diário de bordo com data/hora corrente.
* **`ConsultaHorariosController`**
  * `buscarHorarios(...)`: Carrega todas as partidas e aplica filtros em memória utilizando Java Streams por embarcação, comandante e destino.
  * `obterEmbarcacoes()`, `obterComandantes()`, `obterDestinos()`: Prepara opções para os componentes de seleção (incluindo "Todos").
* **`TecnicoController`**
  * Gestão operacional da oficina mecânica.
  * `obterOSAbertas()`, `obterAlertasHorimetro()`, `obterHistoricoMotores()`, `obterEmbarcacoes()`: Consulta indicadores da oficina.
  * `criarNovaOS(...)` e `finalizarManutencao(...)`: Valida horímetros, datas e custos finais antes de encerrar Ordens de Serviço.

---

### 4.2 Camada DAO (Data Access Object)
Responsável pelo acesso direto ao MySQL via instrução SQL parametrizada (`PreparedStatement`).

* **`ConexaoDAO`**
  * Centraliza as configurações do JDBC (`localhost:3306/gestao_nautica_db`).
  * `obterConexao()`: Carrega o driver `com.mysql.cj.jdbc.Driver` e fornece conexões ativas.
* **`UsuarioDAO`**
  * `autenticar(...)`: Consulta usuário ativo pelo e-mail e valida a senha com o hash criptográfico **BCrypt** (com fallback seguro).
  * `cadastrarUsuario(...)`: Aplica salt + hash BCrypt na senha e grava o novo usuário.
* **`EmbarcacaoDAO`**
  * Executa operaçoes CRUD na tabela `embarcacoes`: `salvar` (INSERT), `atualizar` (UPDATE), `listarTodas` (SELECT) e `excluir` (DELETE).
* **`ManutencaoDAO`**
  * `salvar(...)`: Cadastra ordens de serviço com status padrão 'AGENDADA'.
  * `listarTodasManutencoes()`: Executa `JOIN` entre manutenções e embarcações para exibição em tabelas.
  * `alterarStatus(...)`: Transiciona o estado do ciclo de vida da OS.
* **`TripulanteDAO`**
  * Persistência completa para tripulantes e licenças marítimas.
* **`ViagemDAO`**
  * `salvar(...)`: Mapeia `LocalDateTime` para `Timestamp` SQL e persiste a viagem.
  * `embarcacaoEmViagem(...)` e `comandanteEmViagem(...)`: Garante prevenção contra dupla alocação em viagens ativas.
  * `finalizarViagem(...)`, `cancelarViagem(...)` e `excluirViagem(...)`: Atualiza status operacionais.
* **`AbastecimentoDAO`**
  * Grava os registros de abastecimento, lista dados agregados via `AbastecimentoDTO` e calcula despesas acumuladas.
* **`IncidenteDAO`**
  * Armazena ocorrências de diário de bordo com gravidade e vínculo opcional a viagens (`Types.INTEGER`).
* **`RotaDAO` & `FornecedorDAO`**
  * Consultas auxiliares de leitura para preenchimento de seleções da interface.
* **`ConsultaHorariosDAO`**
  * Consulta SQL com múltiplos `JOINs` agregando embarcação, comandante e rotas para popular o `ConsultaHorarioDTO`.
* **`RelatorioDAO`**
  * `obterResumoCustosPorEmbarcacao()`: Agrega no banco os custos totais provenientes de manutenções e abastecimentos via subconsultas SQL `LEFT JOIN`.
* **`TecnicoDAO`**
  * `concluirOS(...)`: Executa uma transação **ACID** (`conn.setAutoCommit(false)`), atualizando em lote a OS e o horímetro/status da embarcação com suporte a `rollback` em falhas.
  * `listarAlertasHorimetro()`: Retorna embarcações com horímetro próximo (diferença <= 50 horas) da manutenção preventiva.

---

### 4.3 Camada Model (Entidades)
Classes Java POJO que representam o modelo relacional de dados:

* **`Usuario`**: ID, Nome, E-mail, Senha (hash), Perfil (`ADMINISTRADOR`, `OPERADOR`, `TECNICO`) e Status.
* **`Embarcacao`**: ID, Nome, Modelo, Ano de Fabricação, Capacidade de Passageiros, Capacidade de Carga, Horímetro Atual e Status.
* **`Manutencao`**: ID, ID da Embarcação, Tipo (`PREVENTIVA`, `CORRETIVA`), Descrição, Data Agendamento, Data Conclusão, Valor/Custo e Status (`AGENDADA`, `EM_ANALISE`, `EM_ANDAMENTO`, `CONCLUIDA`).
* **`Tripulante`**: ID, Nome, CPF, Matrícula CIR, Categoria Marítima, Data de Vencimento da CIR e Telefone.
* **`Viagem`**: ID, Embarcação, Comandante, Rota/Destino, Quantidade de Passageiros, Data/Hora Partida, Data/Hora Chegada e Status.
* **`Abastecimento`**: ID, Embarcação, Quantidade de Litros, Valor Total, Fornecedor e Data.
* **`Incidente`**: ID, Embarcação, Viagem (opcional), Descrição, Nível de Gravidade (`BAIXA`, `MEDIA`, `ALTA`), Data/Hora e Status.
* **`Fornecedor`**: ID e Nome do Fornecedor de combustíveis/serviços.

---

### 4.4 Camada View (Interface Swing)
Construída em Java Swing com padronização visual, bordas arredondadas e sombras customizadas.

* **`TelaLogin`**: Janela de autenticação com tratamento da tecla ENTER, validação gráfica de credenciais e navegação segura para a tela principal.
* **`TelaPrincipal`**: Dashboard central que ajusta dinamicamente menus, botões e ações conforme o perfil logado (`ADMINISTRADOR`, `OPERADOR`, `TECNICO`). Executa a verificação automática de alertas (`verificarEExibirAlertas`) na inicialização.

---

### 4.5 Camada DTO & Service
* **`DTO (Data Transfer Objects)`**: Transporta dados otimizados entre as camadas sem expor todas as entidades (`CustoEmbarcacaoDTO`, `ConsultaHorarioDTO`, `AbastecimentoDTO`).
* **`Service`**: Módulos responsáveis por rotinas transversais como envio de notificações via e-mail (`EmailService`), validação avançada de documentos e exportação de PDF (`PDFService`).

---

## 5. Regras de Negócio e Segurança

1. **Autenticação Segura (BCrypt):** Senhas nunca são armazenadas em texto simples no banco. O hashing utiliza o algoritmo **BCrypt** com Salt.
2. **RN01 - Restrição de Embarcação:** Nenhuma viagem pode ser registrada se a embarcação selecionada estiver com status `INATIVA` ou `EM_MANUTENCAO`.
3. **RN02 - Alocação Única & Capacidade:** 
   * A mesma embarcação ou o mesmo comandante não podem ser alocados em duas viagens com status `EM_ANDAMENTO` simultaneamente.
   * O número de passageiros informados não pode exceder a capacidade máxima cadastrada para a embarcação.
4. **Controle Transacional de Oficina (ACID):** A conclusão de uma Ordem de Serviço atualiza simultaneamente a tabela de manutenções e o horímetro da embarcação utilizando `conn.setAutoCommit(false)`. Em caso de erro, a operação é revertida (`rollback`).
5. **Avisos Preditivos de Segurança:** Alerta automático no login para documentos (CIR) a vencer em até **15 dias** e manutenções por horímetro a menos de **50 horas** da revisão.

---

## 6. Tecnologias e Bibliotecas

* **Linguagem:** Java 17+ (Orientação a Objetos)
* **GUI Framework:** Java Swing
* **Banco de Dados:** MySQL 8.0+
* **Driver JDBC:** `mysql-connector-j-8.3.0.jar`

### Dependências Locais (`/lib`):
* **`jbcrypt-0.4.jar`**: Criptografia de senhas.
* **`openpdf-1.3.30.jar`**: Gerador de relatórios e exportação PDF.
* **`javax.mail-1.6.2.jar` & `activation-1.1.1.jar`**: Envio de alertas e notificações por e-mail via protocolo SMTP.

---

## 7. Configuração do Banco de Dados e Instalação

### Pré-requisitos
* Java Development Kit (JDK) 17 ou superior.
* MySQL Server 8.0 ou superior instalado.
* IDE com suporte a projetos Java (VS Code, Eclipse, IntelliJ IDEA, NetBeans).

### Passos de Instalação:

1. **Clonar o Repositório:**
   ```bash
   git clone https://github.com/seu-usuario/gestao-nautica.git
   cd gestao-nautica
   ```

2. **Configurar o Banco de Dados:**
   * Abra o MySQL Workbench ou seu gerenciador de banco preferido.
   * Execute o script SQL localizado em `database/script.sql` para criar o banco de dados `gestao_nautica_db` e suas tabelas.

3. **Ajustar Credenciais da Conexão:**
   * Abra o arquivo `src/dao/ConexaoDAO.java`.
   * Atualize os atributos de URL, usuário e senha caso necessário:
     ```java
     private static final String URL = "jdbc:mysql://localhost:3306/gestao_nautica_db";
     private static final String USER = "root";
     private static final String PASS = "sua_senha";
     ```

4. **Configurar o Classpath:**
   * Certifique-se de adicionar todos os arquivos `.jar` presentes no diretório `lib/` às bibliotecas/classpath do projeto na sua IDE.

5. **Executar o Sistema:**
   * Execute a classe `view.TelaLogin` para iniciar a aplicação.

---

## 8. Documentação Adicional

A documentação do projeto está organizada no diretório [`docs/`](./docs/):
* [`01-especificacao-requisitos.md`](./docs/01-especificacao-requisitos.md): Mapeamento detalhado de Requisitos Funcionais (RF) e Não Funcionais (RNF).
* [`02-prototipos-interface.md`](./docs/02-prototipos-interface.md): Telas e protótipos de interface gráfica.
* [`03-planejamento-ms-project.md`](./docs/03-planejamento-ms-project.md): Cronograma de execução e distribuição de tarefas.
* [`04-relatorio-tecnico-final.md`](./docs/04-relatorio-tecnico-final.md): Relatório técnico, diagrama de classes e resultados dos testes.

---

## 9. Licença

Este projeto é desenvolvido para fins acadêmicos e profissionais, estando licenciado sob os termos da licença **MIT**. Veja o arquivo `LICENSE` para mais detalhes.
