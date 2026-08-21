# Protótipos de Interface e Diagramas de Casos de Uso

## 1. Diagrama de Casos de Uso (UML)

O diagrama abaixo ilustra as interações entre os três perfis de usuários e as funcionalidades do sistema:

```mermaid
graph TD
    %% Atores
    Admin((Administrador da Frota))
    Op((Operador / Despachante))
    Tec((Responsável Técnico))

    %% Casos de Uso - Autenticação
    Admin --> UC00[UC00 - Efetuar Login e Selecionar Dashboard]
    Op --> UC00
    Tec --> UC00

    %% Casos de Uso - Administrador
    Admin --> UC01[UC01 - Gerenciar Embarcações]
    Admin --> UC02[UC02 - Gerenciar Tripulação]
    Admin --> UC07[UC07 - Gerar Relatório de Custos]
    Admin --> UC09[UC09 - Gerenciar Validade de Documentos]
    Admin --> UC13[UC13 - Realizar Backup do Banco de Dados]

    %% Casos de Uso - Operador / Despachante
    Op --> UC05[UC05 - Registrar Viagens]
    Op --> UC10[UC10 - Emitir Relatório de Viagens]
    Op --> UC11[UC11 - Registrar Abastecimento]
    Op --> UC12[UC12 - Registrar Incidentes Operacionais]

    %% Casos de Uso - Responsável Técnico
    Tec --> UC03[UC03 - Registrar e Encerrar Manutenções]
    Tec --> UC04[UC04 - Agendar Manutenção Preventiva]
    Tec --> UC06[UC06 - Visualizar Alertas de Revisão]
    Tec --> UC08[UC08 - Consultar Histórico Técnico]

    %% Regras de Inclusão e Extensão
    UC05 .->|include| UC09
    UC03 .->|include| UC06
    UC07 .->|extend| UC14[UC14 - Exportar Relatório em PDF]
    UC10 .->|extend| UC14
```

---

## 2. Protótipos de Interface (Wireframes Textuais)

### 2.1. Tela de Autenticação (Login)
```text
+-------------------------------------------------------------+
|                     GESTAO NAUTICA v1.0                     |
+-------------------------------------------------------------+
|                                                             |
|   Usuario:  [________________________]                      |
|   Senha:    [****************________]                      |
|                                                             |
|   Perfil:   (o) Admin  ( ) Operador  ( ) Técnico            |
|                                                             |
|                  [ Entrar ]    [ Cancelar ]                 |
+-------------------------------------------------------------+
```

### 2.2. Dashboard - Administrador da Frota
```text
+-------------------------------------------------------------+
| GESTAO NAUTICA | Painel Administrador    [Usuario: Vmon-teiro]
+-------------------------------------------------------------+
| [Embarcacoes] [Tripulacao] [Relatorios] [Backup] [Sair]     |
+-------------------------------------------------------------+
| RESUMO DA FROTA                                             |
| +-------------------+  +-------------------+                |
| | Frota Ativa:   12 |  | Doc. Pendentes: 2 |                |
| | Manutencao:     3 |  | Custo Mês: R$ 15k |                |
| +-------------------+  +-------------------+                |
|                                                             |
| ALERTAS DE DOCUMENTACAO (Vencendo em < 15 dias)             |
| - Embarcacao 'Lobo do Mar' - Vistoria vence em: 05 dias     |
| - Vistoria 'Lancha Azul' - Seguro Arrais vence em: 10 dias  |
+-------------------------------------------------------------+
```

### 2.3. Dashboard - Operador / Despachante
```text
+-------------------------------------------------------------+
| GESTAO NAUTICA | Painel Operacional      [Usuario: Operador]|
+-------------------------------------------------------------+
| [Reg. Viagem] [Abastecimento] [Incidentes] [Sair]           |
+-------------------------------------------------------------+
| REGISTRO RAPIDO DE VIAGEM                                   |
| Embarcacao:   [Selecione...        v]                       |
| Comandante:   [Selecione...        v]                       |
| Rota/Destino: [____________________] Passag: [___]           |
| Status Doc:   [ OK - LIBERADO ]                             |
|                                                             |
|                     [ Confirmar Saida ]                     |
+-------------------------------------------------------------+
```

### 2.4. Dashboard - Responsável Técnico pela Manutenção
```text
+-------------------------------------------------------------+
| GESTAO NAUTICA | Painel Tecnico         [Usuario: Tecnico]  |
+-------------------------------------------------------------+
| [Manutencoes] [Agendamentos] [Historico Motor] [Sair]       |
+-------------------------------------------------------------+
| REVISOES PENDENTES / HORIMETRO VENCIDO                      |
| +-----------------+---------------+-----------------------+ |
| | Embarcacao      | Horas Uso     | Proxima Revisao       | |
| +-----------------+---------------+-----------------------+ |
| | Titan II        | 250h          | Troca Oleo (VENCIDA)  | |
| | Nautico I       | 190h          | Filtro (Faltam 10h)   | |
| +-----------------+---------------+-----------------------+ |
|                                                             |
|                   [ Abrir Ordem Servico ]                   |
+-------------------------------------------------------------+
```