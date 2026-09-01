# 📅 Planejamento do Projeto e Cronograma (MS Project)

**Projeto:** Sistema de Gestão Náutica e Manutenção de Embarcações  
**Documento:** `docs/03-planejamento-ms-project.md`  
**Versão:** 1.0  
**Data de Emissão:** Setembro de 2026  
**Gerente do Projeto:** Tech Lead / Coordenador do Projeto  

---

## 📌 1. Visão Geral do Planejamento

Este documento apresenta a linha de base (*baseline*) do cronograma, alocação de recursos, Estrutura Analítica do Projeto (EAP / WBS) e mapeamento do caminho crítico para o desenvolvimento do **Sistema de Gestão Náutica e Manutenção de Embarcações**. 

O projeto foi estruturado para ser executado em um período total de **12 semanas (3 meses)**, adotando uma metodologia híbrida:
* **Planejamento e Escopo:** Metodologia preditiva (Waterfall via MS Project) para definição de marcos, interdependências e baseline.
* **Execução:** Sprints quinzenais (Scrum) para desenvolvimento iterativo das camadas **MVC (Model-View-Controller)**, **DAO** e interfaces gráficos **Java Swing**.

---

## 🏗️ 2. Estrutura Analítica do Projeto (EAP / WBS)

```text
1. SISTEMA DE GESTÃO NÁUTICA
   ├── 1.1 Iniciação e Engenharia de Requisitos
   │   ├── 1.1.1 Elicitação de Requisitos e Regras de Negócio (RN01, RN02)
   │   ├── 1.1.2 Elaboração do Documento de Especificação
   │   └── 1.1.3 Validação de Requisitos e Kickoff
   ├── 1.2 Arquitetura de Software e Banco de Dados
   │   ├── 1.2.1 Modelagem ER do Banco de Dados MySQL
   │   ├── 1.2.2 Criação dos Scripts DDL e DML (`database/script.sql`)
   │   └── 1.2.3 Definição da Estrutura MVC, DAO, DTO e Conexão JDBC
   ├── 1.3 Desenvolvimento do Core do Sistema (Backend & Persistência)
   │   ├── 1.3.1 Camada Model (Entidades POJO)
   │   ├── 1.3.2 Camada DAO e Controle Transacional ACID (BCrypt, PreparedStatement)
   │   ├── 1.3.3 Camada Controller e Validações
   │   └── 1.3.4 Serviços Auxiliares (EmailService, PDFService)
   ├── 1.4 Desenvolvimento da Interface Gráfica (Frontend Swing)
   │   ├── 1.4.1 Prototaipagem de Telas e Design System
   │   ├── 1.4.2 Implementação das Telas de Autenticação e Perfis (`TelaLogin`)
   │   ├── 1.4.3 Dashboard Principal e Menus Dinâmicos por Perfil (`TelaPrincipal`)
   │   └── 1.4.4 Módulos Operacionais (Viagens, Manutenções, Alertas, Consultas)
   ├── 1.5 Testes, Qualidade e Integração
   │   ├── 1.5.1 Testes Unitários e de Regras de Negócio (RN01 e RN02)
   │   ├── 1.5.2 Testes de Segurança (BCrypt, Sanitização e SQL Injection)
   │   ├── 1.5.3 Testes Transacionais ACID na Oficina Mecânica
   │   └── 1.5.4 Homologação e Ajustes de Interface
   └── 1.6 Implantação e Documentação
       ├── 1.6.1 Elaboração do Manual do Usuário e README.md
       ├── 1.6.2 Configuração dos Pacotes (.jar) e Scripts de Deploy
       └── 1.6.3 Encerramento do Projeto e Apresentação Final
```

---

## 🗓️ 3. Tabela Detalhada de Atividades e Prazos

| ID WBS | Atividade / Tarefa | Duração (Dias) | Início | Término | Predecessoras | Recursos Alocados |
| :--- | :--- | :---: | :---: | :---: | :---: | :--- |
| **1.1** | **Iniciação e Requisitos** | **8d** | **Sem 1** | **Sem 2** | - | Analista de Requisitos, Tech Lead |
| 1.1.1 | Levantamento de processos náuticos e requisitos | 4d | Sem 1 | Sem 1 | - | Analista de Requisitos |
| 1.1.2 | Especificação de Requisitos e Regras de Negócio | 3d | Sem 1 | Sem 2 | 1.1.1 | Analista de Requisitos |
| 1.1.3 | Aprovação da Especificação e Kickoff | 1d | Sem 2 | Sem 2 | 1.1.2 | Tech Lead, Cliente |
| **1.2** | **Arquitetura e Banco de Dados** | **7d** | **Sem 2** | **Sem 3** | **1.1.3** | DBA, Arquito de Software |
| 1.2.1 | Modelagem Conceitual e Lógica ER (MySQL 8.0) | 3d | Sem 2 | Sem 3 | 1.1.3 | DBA |
| 1.2.2 | Criação de Scripts DDL/DML e Carga Inicial | 2d | Sem 3 | Sem 3 | 1.2.1 | DBA |
| 1.2.3 | Configuração da Arquitetura MVC/DAO e JDBC | 2d | Sem 3 | Sem 3 | 1.2.2 | Arquiteto de Software |
| **1.3** | **Desenvolvimento Backend & DAO** | **20d** | **Sem 3** | **Sem 7** | **1.2.3** | Dev Java Sr, Dev Java Pl |
| 1.3.1 | Implementação das Entidades (Model) | 3d | Sem 3 | Sem 4 | 1.2.3 | Dev Java Pl |
| 1.3.2 | Implementação do Hashing BCrypt e `UsuarioDAO` | 3d | Sem 4 | Sem 4 | 1.3.1 | Dev Java Sr |
| 1.3.3 | Implementação dos DAOs CRUD (`EmbarcacaoDAO`, etc.) | 5d | Sem 4 | Sem 5 | 1.3.2 | Dev Java Pl |
| 1.3.4 | Implementação de `ViagemDAO` com validações RN01/RN02 | 4d | Sem 5 | Sem 6 | 1.3.3 | Dev Java Sr |
| 1.3.5 | Implementação de `TecnicoDAO` com Transações ACID | 3d | Sem 6 | Sem 6 | 1.3.4 | Dev Java Sr |
| 1.3.6 | Implementação de `PDFService` e `EmailService` | 2d | Sem 6 | Sem 7 | 1.3.5 | Dev Java Pl |
| **1.4** | **Desenvolvimento Frontend (Swing)** | **20d** | **Sem 5** | **Sem 9** | **1.3.3** | Designer UI/UX, Dev Java Pl |
| 1.4.1 | Prototipagem das telas e padronização visual Swing | 4d | Sem 5 | Sem 5 | 1.2.3 | Designer UI/UX |
| 1.4.2 | Tela de Login e Integração com `UsuarioController` | 3d | Sem 5 | Sem 6 | 1.3.2, 1.4.1 | Dev Java Pl |
| 1.4.3 | Tela Principal e Dashboard por Perfil (RBAC) | 5d | Sem 6 | Sem 7 | 1.4.2 | Dev Java Pl |
| 1.4.4 | Telas de Gestão (Embarcações, Tripulantes, Viagens) | 5d | Sem 7 | Sem 8 | 1.4.3 | Dev Java Pl |
| 1.4.5 | Módulo de Oficina, Horímetros e Alertas Preditivos | 3d | Sem 8 | Sem 9 | 1.3.5, 1.4.4 | Dev Java Sr |
| **1.5** | **Testes, QA e Validação** | **10d** | **Sem 9** | **Sem 11** | **1.4.5** | Engenheiro de QA, Dev Java Sr |
| 1.5.1 | Testes Funcionais e Regras de Negócio (RN01/RN02) | 3d | Sem 9 | Sem 9 | 1.4.5 | Engenheiro de QA |
| 1.5.2 | Testes de Criptografia, Segurança e Injeção SQL | 2d | Sem 9 | Sem 10 | 1.5.1 | Dev Java Sr |
| 1.5.3 | Testes de Integridade ACID e Rollback na Oficina | 2d | Sem 10 | Sem 10 | 1.5.2 | Dev Java Sr |
| 1.5.4 | Homologação e Resolução de Bugs | 3d | Sem 10 | Sem 11 | 1.5.3 | Engenheiro de QA, Devs |
| **1.6** | **Documentação e Encerramento** | **5d** | **Sem 11** | **Sem 12** | **1.5.4** | Tech Lead, Documentador |
| 1.6.1 | Redação do Relatório Técnico e README.md | 3d | Sem 11 | Sem 12 | 1.5.4 | Documentador |
| 1.6.2 | Empacotamento de Dependências e Build Final | 1d | Sem 12 | Sem 12 | 1.6.1 | Tech Lead |
| 1.6.3 | Apresentação Final e Entrega do Projeto | 1d | Sem 12 | Sem 12 | 1.6.2 | Toda a Equipe |

---

## 📊 4. Visão do Cronograma (Gráfico de Gantt Simulado)

```text
Atividade / Semana           | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 |S10 |S11 |S12 |
-----------------------------------------------------------------------------------------
1.1 Requisitos & Kickoff     |████████|    |    |    |    |    |    |    |    |    |    |    |
1.2 Modelagem BD & Arch      |    |████████|    |    |    |    |    |    |    |    |    |    |
1.3 Backend & DAO            |    |    |████████████████████████|    |    |    |    |    |    |
1.4 Frontend Swing           |    |    |    |    |████████████████████████|    |    |    |    |
1.5 Testes & QA              |    |    |    |    |    |    |    |    |████████████|    |    |
1.6 Documentação & Deploy    |    |    |    |    |    |    |    |    |    |    |████████|
-----------------------------------------------------------------------------------------
Marcos Principais (Milestones):
 ◆ M1: Escopo Aprovado (Semana 2)
 ◆ M2: Banco de Dados & Arquitetura Prontos (Semana 3)
 ◆ M3: Core Backend e DAO Concluídos (Semana 7)
 ◆ M4: Interface Gráfica Integrada (Semana 9)
 ◆ M5: Sistema Homologado nos Testes (Semana 11)
 ◆ M6: Entrega Final do Projeto (Semana 12)
```

---

## 👥 5. Gestão e Alocação de Recursos

| Função / Papel | Integrantes | Esforço Total (Horas) | Atividades Principais |
| :--- | :--- | :---: | :--- |
| **Tech Lead / Arquiteto** | 1 Profissional | 120h | Arquitetura MVC, validação de segurança, transações ACID e coordenação. |
| **Desenvolvedor Java Sr** | 1 Profissional | 160h | Regras de Negócio (RN01/RN02), BCrypt, `ViagemDAO`, `TecnicoDAO` e integração. |
| **Desenvolvedor Java Pl** | 1 Profissional | 180h | Entidades POJO, DAOs básicos, telas em Java Swing e DTOs. |
| **DBA / Modelador** | 1 Profissional | 60h | Modelagem do MySQL, scripts DDL/DML e otimização de queries com JOINs. |
| **Engenheiro de QA / Testes** | 1 Profissional | 80h | Execução do plano de testes, validação funcional, segurança e carga. |
| **Designer UI/UX & Doc** | 1 Profissional | 60h | Protótipos Swing, layout da interface e documentação técnica em Markdown. |

---

## ⚠️ 6. Análise de Riscos e Caminho Crítico

### Caminho Crítico (Critical Path)
O caminho crítico do projeto compreende a sequência de tarefas que não possuem folga de prazo:
$$	ext{Requisitos (1.1)} 
ightarrow 	ext{Modelagem BD (1.2)} 
ightarrow 	ext{Core Backend/DAO (1.3)} 
ightarrow 	ext{Interface Swing (1.4)} 
ightarrow 	ext{Testes QA (1.5)} 
ightarrow 	ext{Deploy (1.6)}$$
*Qualquer atraso na camada de persitência (DAO) ou na integração Swing afeta diretamente a data de entrega final.*

### Matriz de Riscos e Mitigações

| Risco Identificado | Impacto | Probabilidade | Plano de Mitigação / Ação Preventiva |
| :--- | :---: | :---: | :--- |
| **Incompatibilidade de bibliotecas de terceiros (BCrypt / OpenPDF)** | Médio | Baixa | Utilização de dependências locais testadas no diretório `/lib` com verificação de compatibilidade com Java 17. |
| **Falha na concorrência em transações da oficina mecânica** | Alto | Média | Implementação de isolamento transacional JDBC com `conn.setAutoCommit(false)` e blocos de `try-catch-rollback`. |
| **Lentidão em consultas com múltiplos JOINs** | Média | Baixa | Criação de índices no MySQL para chaves estrangeiras em `viagens`, `manutencoes` e `incidentes`. |
| **Erros de interface gráfica em diferentes resoluções** | Baixo | Média | Padronização dos layouts Swing utilizando Gerenciadores de Layout robustos (`BorderLayout`, `GridBagLayout`) e dimensionamento adaptativo. |
