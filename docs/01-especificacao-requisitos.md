# Especificação de Requisitos - Sistema de Gestão Náutica e Manutenção

## 1. Descrição Geral do Sistema
O **Sistema de Gestão Náutica e Manutenção de Embarcações** é uma aplicação desktop desenvolvida em Java (POO), estruturada na arquitetura MVC/DAO e integrada ao banco de dados MySQL via JDBC. 

O sistema visa centralizar o controle operacional e técnico de frotas fluviais/marítimas, abrangendo o gerenciamento de tripulação, registro de viagens, monitoramento de abastecimentos, controle de incidentes, além do agendamento e histórico de manutenções preventivas e corretivas com alertas de vencimento de documentos e revisões.

---

## 2. Perfis de Usuário e Stakeholders

O sistema possui controle de acesso (Níveis de Permissão), direcionando cada perfil de usuário para uma **Dashboard específica** com as métricas e atalhos relevantes à sua função:

### 2.1. Administrador(a) da Frota
* **Papel:** Gestão macro do sistema, controle financeiro de custos e segurança dos dados.
* **Dashboard Dedicada:** Visão geral da frota (total de embarcações ativas/inativas), gráfico de custos totais de manutenção e combustível, alertas de documentos a vencer, botão de backup do sistema e gestão de acessos.

### 2.2. Operador(a) / Despachante
* **Papel:** Gestão das operações diárias de navegação e logística.
* **Dashboard Dedicada:** Próximas saídas e chegadas, registro rápido de viagens, lista de tripulantes disponíveis, registro de abastecimentos e alertas de incidentes pendentes de resolução.

### 2.3. Responsável Técnico pela Manutenção
* **Papel:** Gestão da integridade física e mecânica das embarcações.
* **Dashboard Dedicada:** Alertas de manutenções vencidas e próximas do vencimento, ordens de serviço em aberto, cronograma de revisões preventivas e histórico técnico de motores e peças.

---

## 3. Requisitos Funcionais (RF)

* **RF00. Autenticação e Controle de Acesso:** O sistema deve permitir o login de usuários e direcioná-los para a Dashboard correspondente ao seu perfil (Administrador, Operador/Despachante, Responsável Técnico).
* **RF01. Cadastrar Embarcações:** Permitir o cadastro, alteração, consulta e inativação de embarcações (nome, modelo, capacidade de carga/passageiros, ano, horímetro/odômetro e documentação associada).
* **RF02. Cadastrar Tripulação:** Gerenciar dados dos tripulantes (pilotos, condutores fluviais, marinheiros) incluindo nome, CPF, registro de habilitação (CIR/Arrais/Mestre) e validade da carteira.
* **RF03. Registrar Manutenções:** Registrar manutenções preventivas e corretivas por embarcação, detalhando serviços executados, peças trocadas e custos.
* **RF04. Agendar Manutenções:** Agendar as próximas revisões preventivas com base na data ou no horímetro/horas de uso da embarcação.
* **RF05. Registrar Viagens Realizadas:** Cadastrar saídas e chegadas (rota, data/horário de partida e término, quantidade de passageiros e comandante responsável).
* **RF06. Alerta de Manutenções:** Notificar e alertar no sistema manutenções que estejam vencidas ou próximas do vencimento estipulado.
* **RF07. Relatório de Custos de Manutenção:** Gerar relatórios com o somatório de custos de manutenção por embarcação em determinado período.
* **RF08. Consultar Histórico de Manutenções:** Permitir a busca do histórico completo de intervenções técnicas de uma embarcação específica.
* **RF09. Controle de Validade de Documentação:** Monitorar e alertar sobre prazos de vencimento de documentos obrigatórios (vistorias da Capitania dos Portos, seguros obrigatórios, licenças ambientais).
* **RF10. Relatório de Viagens:** Emitir relatórios analíticos de viagens realizadas por período, rota ou embarcação.
* **RF11. Registrar Abastecimento:** Registrar abastecimentos de combustível (data, litros abastecidos, valor total, posto/fornecedor e embarcação).
* **RF12. Registrar Incidentes:** Registrar ocorrências, avarias ou problemas identificados durante as viagens para análise da equipe técnica.
* **RF13. Backup Manual do Banco de Dados:** Permitir que o perfil Administrador gere uma cópia de segurança (`.sql`) do banco de dados local.
* **RF14. Exportar Relatórios em PDF:** Permitir a exportação dos relatórios gerados em formato PDF formatado para impressão ou envio.

---

## 4. Requisitos Não Funcionais (RNF)

* **RNF01. Interface Gráfica Responsiva por Perfil:** Interface desenvolvida em Java (JavaFX ou Swing) ajustada ao perfil do usuário logado.
* **RNF02. Persistência em Banco de Dados:** Utilização do SGBD MySQL (via ambiente XAMPP) com conexão JDBC nativa e padrão DAO.
* **RNF03. Arquitetura Modular (POO/MVC):** Código organizado estritamente no padrão MVC (Model, View, Controller), utilizando os pilares da Orientação a Objetos (Encapsulamento, Herança, Polimorfismo e Abstração).
* **RNF04. Segurança de Credenciais:** Criptografia/hash para senhas de acesso armazenadas no banco de dados.
* **RNF05. Desempenho:** Consultas e geração de relatórios em telas do sistema com tempo de resposta inferior a 2 segundos.

---

## 5. Regras de Negócio (RN)

* **RN01. Impedimento por Documento Vencido:** Não permitir a liberação de registros de novas viagens (RF05) para embarcações com documentos ou vistorias vencidas (RF09).
* **RN02. Vinculação Obrigatória de Tripulante Habilitado:** Toda viagem registrada deve possuir obrigatoriamente pelo menos um condutor/piloto com habilitação válida associada.
* **RN03. Alertas Automáticos na Dashboard:** Ao realizar o login, a Dashboard do perfil correspondente deve exibir automaticamente pop-ups ou banners com os alertas críticos (documentos ou manutenções a vencer nos próximos 15 dias).
* **RN04. Integridade de Registros (Exclusão Lógica):** Embarcações que já possuem histórico de viagens, abastecimentos ou manutenções registradas não poderão ser excluídas do banco de dados, apenas desativadas.
* **RN05. Privilégio Exclusivo de Backup:** A rotina de backup manual (RF13) é de acesso restrito ao perfil Administrador.