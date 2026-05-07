<div align="center">

<h1>📚 EduFlow</h1>

**Sistema de Gerenciamento de Monitorias Acadêmicas**

[![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![JavaFX](https://img.shields.io/badge/JavaFX-17.0.2-blue?style=for-the-badge)](https://openjfx.io/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-42.7.3-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)

<br/>

> Projeto acadêmico desenvolvido na disciplina de **Análise e Projeto de Sistemas** do curso de **ADS — IFPB Campus Esperança**.  
> Simula o funcionamento real de um sistema de monitorias institucionais, cobrindo desde o agendamento de sessões até a validação de horas pelo professor.

</div>

---

## 📋 Índice

- [Sobre o Projeto](#-sobre-o-projeto)
- [Funcionalidades](#-funcionalidades)
- [Arquitetura](#-arquitetura)
- [Regras de Negócio](#-regras-de-negócio)
- [Pré-requisitos](#-pré-requisitos)
- [Configuração do Banco de Dados](#-configuração-do-banco-de-dados)
- [Instalação e Execução](#-instalação-e-execução)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Diagramas UML](#-diagramas-uml)
- [Papéis de Usuário](#-papéis-de-usuário)
- [Tecnologias e Padrões](#-tecnologias-e-padrões)
- [Equipe](#-equipe)

---

## 🎯 Sobre o Projeto

O **EduFlow** é uma aplicação desktop multiplataforma (Windows, Linux, macOS) que digitaliza e organiza o ciclo completo de monitorias acadêmicas. O sistema conecta alunos, monitores, professores e administradores em torno de um banco de dados PostgreSQL centralizado, eliminando a desorganização típica da gestão manual de monitorias.

**O problema resolvido:** processos de monitoria conduzidos de forma informal (mensagens, planilhas avulsas, sem rastreabilidade) resultam em falhas na comunicação, dificuldade na validação de horas e ausência de dados analíticos para gestores acadêmicos.

**A solução:** um sistema estruturado com controle de acesso por perfil, fluxo de agendamento com validação em tempo real, registro de atendimentos e dashboards analíticos.

---

## ✅ Funcionalidades

### Para o Aluno
- Consulta ao catálogo de disciplinas disponíveis para monitoria
- Visualização de horários disponíveis com vagas em tempo real
- Agendamento de sessões com descrição do assunto a ser tratado
- Cancelamento de agendamentos (sem restrição de horário)
- Histórico completo de monitorias realizadas

### Para o Monitor
- Cadastro e gerenciamento da agenda de horários (local físico ou link de videoconferência)
- Registro de atendimentos (presença + conteúdo trabalhado)
- Cancelamento de agendamentos com justificativa (mín. 2h de antecedência)
- Relatório mensal de horas para validação pelo professor
- Dashboard de engajamento das suas monitorias

### Para o Professor
- Vinculação de monitores às disciplinas sob sua responsabilidade
- Validação (aprovação) dos relatórios de horas dos monitores
- Painel analítico de demanda por disciplina e engajamento de alunos
- Acesso ao histórico de atendimentos da disciplina

### Para o Administrador
- CRUD completo de usuários (alunos, monitores, professores)
- CRUD do catálogo de disciplinas
- Gerenciamento do semestre letivo ativo
- Visão consolidada do sistema

---

## 🏗️ Arquitetura

O EduFlow segue uma **Arquitetura em Camadas** com separação clara de responsabilidades:

```
┌──────────────────────────────────────────────┐
│                  View (FXML)                  │  ← Telas JavaFX + CSS
├──────────────────────────────────────────────┤
│              Controller (UI)                  │  ← Manipulação de eventos
├──────────────────────────────────────────────┤
│                  Service                      │  ← Regras de negócio
├──────────────────────────────────────────────┤
│                 Repository                    │  ← Acesso a dados (JDBC)
├──────────────────────────────────────────────┤
│           Domain (Entities + Enums)           │  ← Modelo Rico de domínio
├──────────────────────────────────────────────┤
│          PostgreSQL (Neon / local)            │  ← Banco de dados
└──────────────────────────────────────────────┘
```

**Design Patterns utilizados:**
- **Singleton** — `ConectionFactory` e `SessionManager`
- **Repository Pattern** — abstração da persistência via JDBC
- **Rich Domain Model** — lógica de negócio encapsulada nas entidades
- **Dependency Injection Manual** — via construtor, sem framework
- **JavaFX Properties** — binding de dados na interface

---

## 📏 Regras de Negócio

| Código | Nome | Descrição |
|--------|------|-----------|
| **RN01** | Conflito de horários | Um aluno não pode agendar duas monitorias sobrepostas no mesmo dia |
| **RN02** | Validação de horas | Somente o professor responsável pela disciplina pode validar as horas — nunca o próprio monitor |
| **RN03** | Disponibilidade de vagas | Agendamento só é permitido se houver vagas disponíveis na agenda |
| **RN04** | Bloqueio por faltas | Alunos com 3 ou mais faltas sem aviso ficam bloqueados por 7 dias |
| **RN05** | Prazo de cancelamento | Cancelamentos (aluno ou monitor) exigem mínimo de 2 horas de antecedência |
| **RN06** | Registro obrigatório | O monitor deve registrar presença e conteúdo para que as horas sejam contabilizadas |
| **RN07** | Vínculo obrigatório | Monitor só pode cadastrar horários para disciplinas às quais está formalmente vinculado |
| **RN08** | Auto-agendamento | Monitor não pode agendar monitoria como aluno nos seus próprios horários |
| **RN09** | Contagem de horas | Apenas atendimentos com status `VALIDADO` entram no relatório mensal do monitor |
| **RN10** | Localidade da sessão | Todo horário cadastrado deve ter local físico (sala) ou link de videoconferência |

---

## 🔧 Pré-requisitos

Antes de clonar e executar o projeto, certifique-se de ter instalado:

| Ferramenta | Versão Mínima | Download |
|------------|---------------|----------|
| JDK (Java Development Kit) | 17+ | [adoptium.net](https://adoptium.net/) |
| Apache Maven | 3.8+ | [maven.apache.org](https://maven.apache.org/download.cgi) |
| PostgreSQL | 14+ (ou conta Neon) | [postgresql.org](https://www.postgresql.org/download/) |
| Git | qualquer | [git-scm.com](https://git-scm.com/) |

> **Nota sobre JavaFX:** as dependências do JavaFX são gerenciadas automaticamente pelo Maven — não é necessária instalação separada.

---

## 🗄️ Configuração do Banco de Dados

### Opção 1 — PostgreSQL Local (recomendado para testes)

1. Instale o PostgreSQL e crie um banco de dados:

```sql
CREATE DATABASE eduflow;
```

2. Execute o script de criação das tabelas disponível em [`database/schema.sql`](database/schema.sql) (incluído neste repositório).

3. Opcionalmente, popule com dados de exemplo:

```sql
-- =============================================================================
--  EduFlow — Script de Criação do Banco de Dados
--  Sistema de Gerenciamento de Monitorias Acadêmicas
--  IFPB Campus Esperança — ADS — Disciplina: Análise e Projeto de Sistemas
-- =============================================================================
--  Compatível com: PostgreSQL 14+
--  Executar como: psql -U <usuario> -d <banco> -f schema.sql
--  Ou cole diretamente no SQL Editor do Neon / pgAdmin / DBeaver
-- =============================================================================

-- Limpa o schema caso já exista (útil para reset em ambiente de desenvolvimento)
DROP TABLE IF EXISTS atendimento        CASCADE;
DROP TABLE IF EXISTS agendamento        CASCADE;
DROP TABLE IF EXISTS agenda             CASCADE;
DROP TABLE IF EXISTS aula               CASCADE;
DROP TABLE IF EXISTS aluno_disciplina   CASCADE;
DROP TABLE IF EXISTS monitor_disciplina CASCADE;
DROP TABLE IF EXISTS professor_disciplina CASCADE;
DROP TABLE IF EXISTS disciplina         CASCADE;
DROP TABLE IF EXISTS usuario            CASCADE;

-- =============================================================================
--  TABELA: usuario
--  Armazena todos os tipos de usuário. O campo "role" define o perfil de acesso.
--  Valores aceitos: 'ALUNO', 'MONITOR', 'PROFESSOR', 'ADMINISTRADOR'
--  Monitor herda de Aluno — diferenciado apenas pelo role.
-- =============================================================================

CREATE TABLE usuario (
    id              BIGSERIAL       PRIMARY KEY,
    nome            VARCHAR(150)    NOT NULL,
    email           VARCHAR(255)    NOT NULL UNIQUE,
    senha_hash      VARCHAR(255)    NOT NULL,           -- BCrypt hash
    matricula       VARCHAR(20)     NOT NULL UNIQUE,
    role            VARCHAR(20)     NOT NULL
        CONSTRAINT chk_usuario_role CHECK (role IN ('ALUNO', 'MONITOR', 'PROFESSOR', 'ADMINISTRADOR')),
    ativo           BOOLEAN         NOT NULL DEFAULT TRUE,

    -- Campos exclusivos de Aluno / Monitor (RN04)
    faltas_acumuladas   INTEGER     NOT NULL DEFAULT 0,
    bloqueado_ate       DATE        NULL,               -- NULL = não bloqueado

    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE  usuario                   IS 'Tabela unificada de usuários (ALUNO, MONITOR, PROFESSOR, ADMINISTRADOR).';
COMMENT ON COLUMN usuario.senha_hash        IS 'Hash BCrypt da senha. Nunca armazene senha em texto plano.';
COMMENT ON COLUMN usuario.role              IS 'Perfil de acesso. Valores: ALUNO, MONITOR, PROFESSOR, ADMINISTRADOR.';
COMMENT ON COLUMN usuario.faltas_acumuladas IS 'Contador de faltas sem aviso. Ao atingir 3 aplica bloqueio de 7 dias (RN04).';
COMMENT ON COLUMN usuario.bloqueado_ate     IS 'Data de fim do bloqueio por faltas. NULL = sem bloqueio ativo (RN04).';

-- =============================================================================
--  TABELA: disciplina
--  Catálogo acadêmico de disciplinas gerenciado pelo Administrador.
-- =============================================================================

CREATE TABLE disciplina (
    id              BIGSERIAL       PRIMARY KEY,
    nome            VARCHAR(150)    NOT NULL,
    codigo          VARCHAR(20)     NOT NULL UNIQUE,
    semestre_letivo VARCHAR(10)     NOT NULL,           -- Ex: "2026.1"
    ativo           BOOLEAN         NOT NULL DEFAULT TRUE,

    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE disciplina IS 'Catálogo de disciplinas. Gerenciado pelo Administrador.';

-- =============================================================================
--  TABELA: professor_disciplina
--  Vinculação N:M entre Professor e Disciplina.
--  RN02: apenas professores vinculados podem validar horas de monitores.
-- =============================================================================

CREATE TABLE professor_disciplina (
    professor_id    BIGINT  NOT NULL REFERENCES usuario(id)    ON DELETE CASCADE,
    disciplina_id   BIGINT  NOT NULL REFERENCES disciplina(id) ON DELETE CASCADE,
    PRIMARY KEY (professor_id, disciplina_id)
);

-- =============================================================================
--  TABELA: monitor_disciplina
--  Vinculação N:M entre Monitor e Disciplina.
--  RN07: monitor só pode criar agenda para disciplinas vinculadas.
-- =============================================================================

CREATE TABLE monitor_disciplina (
    monitor_id      BIGINT  NOT NULL REFERENCES usuario(id)    ON DELETE CASCADE,
    disciplina_id   BIGINT  NOT NULL REFERENCES disciplina(id) ON DELETE CASCADE,
    PRIMARY KEY (monitor_id, disciplina_id)
);

-- =============================================================================
--  TABELA: aluno_disciplina
--  Vinculação N:M entre Aluno e Disciplina (matrícula em disciplinas).
-- =============================================================================

CREATE TABLE aluno_disciplina (
    aluno_id        BIGINT  NOT NULL REFERENCES usuario(id)    ON DELETE CASCADE,
    disciplina_id   BIGINT  NOT NULL REFERENCES disciplina(id) ON DELETE CASCADE,
    PRIMARY KEY (aluno_id, disciplina_id)
);

-- =============================================================================
--  TABELA: agenda
--  Slot de monitoria cadastrado pelo Monitor.
--  RN10: local OU link são obrigatórios (validado pela constraint CHECK).
-- =============================================================================

CREATE TABLE agenda (
    id                  BIGSERIAL       PRIMARY KEY,
    monitor_id          BIGINT          NOT NULL REFERENCES usuario(id)    ON DELETE CASCADE,
    disciplina_id       BIGINT          NOT NULL REFERENCES disciplina(id) ON DELETE CASCADE,
    data_hora_inicio    TIMESTAMPTZ     NOT NULL,
    data_hora_fim       TIMESTAMPTZ     NOT NULL,
    local               VARCHAR(200)    NULL,           -- sala física (RN10)
    link                VARCHAR(500)    NULL,           -- videoconferência (RN10)
    vagas_totais        INTEGER         NOT NULL DEFAULT 1,
    vagas_ocupadas      INTEGER         NOT NULL DEFAULT 0,
    cancelada           BOOLEAN         NOT NULL DEFAULT FALSE,
    justificativa       TEXT            NULL,           -- preenchida ao cancelar

    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    -- RN10: pelo menos local ou link deve ser informado
    CONSTRAINT chk_local_ou_link CHECK (
        local IS NOT NULL AND TRIM(local) <> ''
        OR
        link  IS NOT NULL AND TRIM(link)  <> ''
    ),

    -- Vagas coerentes
    CONSTRAINT chk_vagas CHECK (
        vagas_ocupadas >= 0
        AND vagas_totais >= 1
        AND vagas_ocupadas <= vagas_totais
    ),

    -- Horário válido
    CONSTRAINT chk_horario CHECK (data_hora_fim > data_hora_inicio)
);

COMMENT ON TABLE  agenda       IS 'Slot de monitoria aberto pelo Monitor. RN10, RN07.';
COMMENT ON COLUMN agenda.local IS 'Sala ou espaço físico. Obrigatório se link for nulo (RN10).';
COMMENT ON COLUMN agenda.link  IS 'URL de videoconferência. Obrigatório se local for nulo (RN10).';

-- =============================================================================
--  TABELA: agendamento
--  Reserva de um Aluno em uma Agenda de monitoria.
--  Ciclo de status: PENDENTE → CONFIRMADO → REALIZADO/FALTOU → VALIDADO
--  Valores aceitos: 'PENDENTE', 'CONFIRMADO', 'CANCELADO_ALUNO',
--                   'CANCELADO_MONITOR', 'REALIZADO', 'FALTOU', 'VALIDADO'
-- =============================================================================

CREATE TABLE agendamento (
    id                      BIGSERIAL       PRIMARY KEY,
    aluno_id                BIGINT          NOT NULL REFERENCES usuario(id)  ON DELETE CASCADE,
    agenda_id               BIGINT          NOT NULL REFERENCES agenda(id)   ON DELETE CASCADE,
    assunto                 TEXT            NOT NULL,
    data_hora_solicitacao   TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    status                  VARCHAR(20)     NOT NULL DEFAULT 'PENDENTE'
        CONSTRAINT chk_agendamento_status CHECK (status IN (
            'PENDENTE', 'CONFIRMADO', 'CANCELADO_ALUNO',
            'CANCELADO_MONITOR', 'REALIZADO', 'FALTOU', 'VALIDADO'
        )),
    justificativa           TEXT            NULL,   -- preenchida no cancelamento

    -- RN01: impede duplicata exata por aluno/agenda
    CONSTRAINT uq_aluno_agenda UNIQUE (aluno_id, agenda_id)
);

COMMENT ON TABLE  agendamento        IS 'Reserva de um aluno em um slot de monitoria. Ciclo de status controlado pelas RN.';
COMMENT ON COLUMN agendamento.status IS 'Status do agendamento. Valores: PENDENTE, CONFIRMADO, CANCELADO_ALUNO, CANCELADO_MONITOR, REALIZADO, FALTOU, VALIDADO.';

-- =============================================================================
--  TABELA: atendimento
--  Registro do desfecho da monitoria feito pelo Monitor após a sessão.
--  RN06: obrigatório para que as horas fiquem disponíveis para validação.
--  RN09: apenas status VALIDADO conta no relatório mensal.
-- =============================================================================

CREATE TABLE atendimento (
    id                      BIGSERIAL       PRIMARY KEY,
    agendamento_id          BIGINT          NOT NULL UNIQUE REFERENCES agendamento(id) ON DELETE CASCADE,
    conteudo_trabalhado     TEXT            NOT NULL,
    presenca                BOOLEAN         NOT NULL,   -- TRUE = presente, FALSE = faltou
    data_hora_registro      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE atendimento IS 'Registro do desfecho da monitoria. Um atendimento por agendamento (RN06, RN09).';

-- =============================================================================
--  TABELA: aula
--  Calendário de aulas dos Professores.
--  Valores aceitos: 'REGULAR', 'REPOSICAO', 'EXTRA'
-- =============================================================================

CREATE TABLE aula (
    id              BIGSERIAL       PRIMARY KEY,
    professor_id    BIGINT          NOT NULL REFERENCES usuario(id)    ON DELETE CASCADE,
    disciplina_id   BIGINT          NOT NULL REFERENCES disciplina(id) ON DELETE CASCADE,
    data_hora       TIMESTAMPTZ     NOT NULL,
    tipo            VARCHAR(10)     NOT NULL DEFAULT 'REGULAR'
        CONSTRAINT chk_aula_tipo CHECK (tipo IN ('REGULAR', 'REPOSICAO', 'EXTRA')),
    justificativa   TEXT            NULL,   -- obrigatória para REPOSICAO/EXTRA

    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE  aula      IS 'Calendário de aulas do Professor por disciplina.';
COMMENT ON COLUMN aula.tipo IS 'Tipo de aula. Valores: REGULAR, REPOSICAO, EXTRA.';

-- =============================================================================
--  ÍNDICES
--  Aceleram as consultas mais comuns da aplicação.
-- =============================================================================

CREATE INDEX idx_agenda_monitor      ON agenda(monitor_id);
CREATE INDEX idx_agenda_disciplina   ON agenda(disciplina_id);
CREATE INDEX idx_agenda_data_inicio  ON agenda(data_hora_inicio);
CREATE INDEX idx_agendamento_aluno   ON agendamento(aluno_id);
CREATE INDEX idx_agendamento_agenda  ON agendamento(agenda_id);
CREATE INDEX idx_agendamento_status  ON agendamento(status);
CREATE INDEX idx_aula_professor      ON aula(professor_id);
CREATE INDEX idx_aula_disciplina     ON aula(disciplina_id);

-- =============================================================================
--  DADOS DE EXEMPLO
--  Usuários para teste e homologação do sistema.
--  Senhas: todas são "senha123" — hash BCrypt gerado com jbcrypt 0.4
--  Para gerar seu próprio hash: execute GerarHash.java do projeto.
-- =============================================================================

-- Administrador
INSERT INTO usuario (nome, email, senha_hash, matricula, role)
VALUES (
    'Admin Sistema',
    'admin@eduflow.edu.br',
    '$2a$12$K3MiHWPGM3LnGOKpKGIOvuqSUFEBM5w6f9jgbGJFi8mCe7xBIEbzS',
    'ADM0001',
    'ADMINISTRADOR'
);

-- Professor
INSERT INTO usuario (nome, email, senha_hash, matricula, role)
VALUES (
    'Prof. Carlos Andrade',
    'carlos.andrade@eduflow.edu.br',
    '$2a$12$K3MiHWPGM3LnGOKpKGIOvuqSUFEBM5w6f9jgbGJFi8mCe7xBIEbzS',
    'PRF0001',
    'PROFESSOR'
);

-- Monitor
INSERT INTO usuario (nome, email, senha_hash, matricula, role)
VALUES (
    'Lucas Monitor',
    'lucas.monitor@academico.edu.br',
    '$2a$12$K3MiHWPGM3LnGOKpKGIOvuqSUFEBM5w6f9jgbGJFi8mCe7xBIEbzS',
    'MON0001',
    'MONITOR'
);

-- Aluno
INSERT INTO usuario (nome, email, senha_hash, matricula, role)
VALUES (
    'Maria Aluna',
    'maria.aluna@academico.edu.br',
    '$2a$12$K3MiHWPGM3LnGOKpKGIOvuqSUFEBM5w6f9jgbGJFi8mCe7xBIEbzS',
    'ALU0001',
    'ALUNO'
);

-- Disciplinas de exemplo
INSERT INTO disciplina (nome, codigo, semestre_letivo) VALUES
    ('Análise e Projeto de Sistemas',   'APS001', '2026.1'),
    ('Programação Orientada a Objetos', 'POO001', '2026.1'),
    ('Banco de Dados',                  'BD001',  '2026.1'),
    ('Estrutura de Dados',              'ED001',  '2026.1');

-- Vincula professor à disciplina APS
INSERT INTO professor_disciplina (professor_id, disciplina_id)
SELECT u.id, d.id
FROM usuario u, disciplina d
WHERE u.matricula = 'PRF0001' AND d.codigo = 'APS001';

-- Vincula monitor à disciplina APS
INSERT INTO monitor_disciplina (monitor_id, disciplina_id)
SELECT u.id, d.id
FROM usuario u, disciplina d
WHERE u.matricula = 'MON0001' AND d.codigo = 'APS001';

-- Matricula aluno na disciplina APS
INSERT INTO aluno_disciplina (aluno_id, disciplina_id)
SELECT u.id, d.id
FROM usuario u, disciplina d
WHERE u.matricula = 'ALU0001' AND d.codigo = 'APS001';

-- =============================================================================
--  VERIFICAÇÃO FINAL
-- =============================================================================

SELECT 'usuario'              AS tabela, COUNT(*) AS registros FROM usuario
UNION ALL SELECT 'disciplina',             COUNT(*) FROM disciplina
UNION ALL SELECT 'professor_disciplina',   COUNT(*) FROM professor_disciplina
UNION ALL SELECT 'monitor_disciplina',     COUNT(*) FROM monitor_disciplina
UNION ALL SELECT 'aluno_disciplina',       COUNT(*) FROM aluno_disciplina
UNION ALL SELECT 'agenda',                 COUNT(*) FROM agenda
UNION ALL SELECT 'agendamento',            COUNT(*) FROM agendamento
UNION ALL SELECT 'atendimento',            COUNT(*) FROM atendimento
UNION ALL SELECT 'aula',                   COUNT(*) FROM aula;

-- =============================================================================
--  Schema criado com sucesso.
--  Senha de todos os usuários de exemplo: senha123
--  Altere as credenciais antes de usar em produção.


-- USE A CLASSE GerarHash para passar uma senha e obter seu hash.
-- USE O HASH GERADO, e armazene ele no banco de dados para o login.


-- =============================================================================
```

### Opção 2 — Neon (PostgreSQL Serverless na nuvem)

1. Crie uma conta gratuita em [neon.tech](https://neon.tech)
2. Crie um novo projeto e copie a connection string
3. Execute o `schema.sql` pelo SQL Editor do próprio Neon

### Configuração da Conexão

Edite o arquivo `src/main/resources/.env` com suas credenciais:

```env
DB_URL=jdbc:postgresql://<host>/<banco>?sslmode=require
DB_USER=<usuário>
DB_PASSWORD=<senha>
```

**Exemplo para PostgreSQL local:**
```env
DB_URL=jdbc:postgresql://localhost:5432/eduflow
DB_USER=postgres
DB_PASSWORD=sua_senha
```

**Exemplo para Neon:**
```env
DB_URL=jdbc:postgresql://ep-xxxx.us-east-1.aws.neon.tech/eduflow?sslmode=require&channelBinding=require
DB_USER=eduflow_owner
DB_PASSWORD=xxxxxxxxxxxx
```

> ⚠️ **Atenção:** O arquivo `.env` está no `.gitignore` por padrão. Nunca versione credenciais de produção.

---

## 🚀 Instalação e Execução

```bash
# 1. Clone o repositório
git clone https://github.com/seu-usuario/eduflow-javafx.git
cd eduflow-javafx

# 2. Configure o banco de dados (veja seção acima)
#    Edite src/main/resources/.env com suas credenciais

# 3. Execute o schema SQL no seu banco PostgreSQL

# 4. Compile e execute com Maven
mvn clean javafx:run
```

### Execução via IDE (IntelliJ IDEA)

1. Importe o projeto como **Maven Project**
2. Aguarde o download das dependências
3. Configure as variáveis de ambiente no Run/Debug Configuration ou edite o `.env`
4. Execute a classe `MainApp.java`

### Gerando senha com BCrypt

O sistema usa **BCrypt** para hash de senhas. Para criar um usuário administrador inicial, compile e execute a utilitária:

```bash
mvn compile
mvn exec:java -Dexec.mainClass="br.edu.ifpb.esperanca.eduflow.GerarHash"
```

Copie o hash gerado e insira diretamente no banco para o primeiro usuário admin.

---

## 📁 Estrutura do Projeto

```
eduflow-javafx/
├── docs/
│   ├── Documento de Visão - Eduflow.pdf
│   ├── eduflow-javafx - neondb - public.png   ← Modelo Relacional do BD
│   └── uml/
│       ├── EduFlow - Diagrama de Atividade.png
│       ├── EduFlow - Diagrama de Classes.png
│       ├── EduFlow - Diagrama de Estados.png
│       ├── EduFlow - Diagrama de Sequencia.png
│       └── EduFlow-Diagrama de Casos de Uso.png
├── lib/
│   └── jbcrypt-0.4.jar
├── src/
│   └── main/
│       ├── java/br/edu/ifpb/esperanca/eduflow/
│       │   ├── MainApp.java                        ← Entry point
│       │   ├── GerarHash.java                      ← Utilitária BCrypt
│       │   ├── controller/                         ← Controllers JavaFX
│       │   │   ├── AdminDashBoardController.java
│       │   │   ├── AlunoDashBoardController.java
│       │   │   ├── MonitorDashBoardController.java
│       │   │   ├── ProfessorDashboardController.java
│       │   │   ├── LoginController.java
│       │   │   └── CadastroController.java
│       │   ├── domain/
│       │   │   ├── entities/                       ← Modelo Rico de Domínio
│       │   │   │   ├── Usuario.java (abstract)
│       │   │   │   ├── Aluno.java
│       │   │   │   ├── Monitor.java
│       │   │   │   ├── Professor.java
│       │   │   │   ├── Administrador.java
│       │   │   │   ├── Disciplina.java
│       │   │   │   ├── Agenda.java
│       │   │   │   ├── Agendamento.java
│       │   │   │   ├── Atendimento.java
│       │   │   │   └── Aula.java
│       │   │   ├── enums/
│       │   │   │   ├── Role.java
│       │   │   │   └── StatusAgendamento.java
│       │   │   └── exceptions/
│       │   │       ├── BusinessException.java
│       │   │       └── ValidationException.java
│       │   ├── repository/                         ← Persistência JDBC
│       │   │   ├── ConectionFactory.java
│       │   │   ├── UsuarioRepository.java
│       │   │   ├── AgendaRepository.java
│       │   │   ├── AgendamentoRepository.java
│       │   │   ├── AtendimentoRepository.java
│       │   │   ├── AulaRepository.java
│       │   │   ├── DisciplinaRepository.java
│       │   │   ├── AlunoDisciplinaRepository.java
│       │   │   ├── MonitorDisciplinaRepository.java
│       │   │   └── ProfessorDisciplinaRepository.java
│       │   └── service/                            ← Regras de Negócio
│       │       ├── SessionManager.java
│       │       ├── UsuarioService.java
│       │       ├── AgendaService.java
│       │       ├── AgendamentoService.java
│       │       ├── DisciplinaService.java
│       │       ├── AulaService.java
│       │       ├── AlunoDisciplinaService.java
│       │       ├── MonitorDisciplinaService.java
│       │       ├── ProfessorDisciplinaService.java
│       │       └── RelatorioService.java
│       └── resources/
│           ├── .env                                ← Credenciais (não versionar)
│           └── br/edu/ifpb/esperanca/eduflow/view/
│               ├── login.fxml
│               ├── cadastro.fxml
│               ├── admin_dashboard.fxml
│               ├── aluno_dashboard.fxml
│               ├── monitor_dashboard.fxml
│               ├── professor_dashboard.fxml
│               └── styles/
│                   └── eduflow.css
├── database/
│   └── schema.sql                                  ← Script de criação do BD
├── pom.xml
└── README.md
```

---

## 📊 Diagramas UML

Todos os diagramas estão disponíveis na pasta [`docs/uml/`](docs/uml/).

| Diagrama | Descrição |
|----------|-----------|
| [Casos de Uso](docs/uml/EduFlow-Diagrama%20de%20Casos%20de%20Uso.png) | Interações dos atores com o sistema |
| [Diagrama de Classes](docs/uml/EduFlow%20-%20Diagrama%20de%20Classes.png) | Estrutura OO do domínio |
| [Diagrama de Sequência](docs/uml/EduFlow%20-%20Diagrama%20de%20Sequencia.png) | Fluxo de chamadas entre camadas |
| [Diagrama de Atividade](docs/uml/EduFlow%20-%20Diagrama%20de%20Atividade.png) | Fluxos de processo do sistema |
| [Diagrama de Estados](docs/uml/EduFlow%20-%20Diagrama%20de%20Estados.png) | Ciclo de vida do Agendamento |
| [Modelo Relacional](docs/eduflow-javafx%20-%20neondb%20-%20public.png) | Esquema do banco de dados |

---

## 👥 Papéis de Usuário

O sistema utiliza controle de acesso baseado em **Roles**. Cada usuário possui exatamente um papel:

```
ADMINISTRADOR  →  Gerencia usuários e disciplinas
PROFESSOR      →  Valida horas e supervisiona disciplinas
MONITOR        →  Cadastra agenda e registra atendimentos
ALUNO          →  Agenda monitorias e consulta histórico
```

O fluxo de status de um agendamento segue o ciclo:

```
PENDENTE → CONFIRMADO → REALIZADO → VALIDADO
                ↓              ↓
        CANCELADO_ALUNO   CANCELADO_MONITOR
                               ↓
                             FALTOU
```

---

## 🛠️ Tecnologias e Padrões

| Categoria | Tecnologia |
|-----------|-----------|
| Linguagem | Java 17+ |
| Interface Gráfica | JavaFX 17.0.2 (FXML + CSS) |
| Banco de Dados | PostgreSQL 14+ |
| Driver JDBC | PostgreSQL JDBC 42.7.3 |
| Hash de Senha | jBCrypt 0.4 |
| Build | Apache Maven |
| Hospedagem BD (demo) | Neon (PostgreSQL Serverless) |
| Versionamento | Git + GitHub |

---

## 👨‍💻 Equipe

Desenvolvido pela **Equipe APS 2026.1** — IFPB Campus Esperança

| Nome | Contato |
|------|---------|
| Lucas Barbosa Graciano | barbosa.graciano@academico.edu.br |
| Valdenio Pantaleao dos Santos | — |
| Mateus Miranda da Silva | — |

**Disciplina:** Análise e Projeto de Sistemas  
**Curso:** Tecnologia em Análise e Desenvolvimento de Sistemas (ADS)  
**Instituição:** IFPB — Instituto Federal da Paraíba, Campus Esperança  
**Semestre:** 2026.1

---

## 📄 Licença

Este projeto é distribuído sob a licença **MIT**. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

```
MIT License — sinta-se livre para usar, modificar e distribuir.
Créditos ao projeto original são sempre bem-vindos. 🎓
```

---

<div align="center">
  <sub>Feito com ☕ e Java por estudantes de ADS do IFPB Esperança</sub>
</div>