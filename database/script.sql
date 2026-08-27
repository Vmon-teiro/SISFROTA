-- ============================================================
-- SCRIPT DE CRIAÇÃO DO BANCO DE DADOS - GESTÃO NÁUTICA
-- Banco de Dados: MySQL (XAMPP / phpMyAdmin)
-- Arquitetura: JDBC / Java DAO
-- ============================================================

CREATE DATABASE IF NOT EXISTS gestao_nautica_db 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE gestao_nautica_db;

-- ------------------------------------------------------------
-- DROPS PREVENTIVOS (Ordem inversa das chaves estrangeiras)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS incidentes;
DROP TABLE IF EXISTS abastecimentos;
DROP TABLE IF EXISTS manutencoes;
DROP TABLE IF EXISTS viagens;
DROP TABLE IF EXISTS rotas;
DROP TABLE IF EXISTS documentos_embarcacao;
DROP TABLE IF EXISTS tripulantes;
DROP TABLE IF EXISTS embarcacoes;
DROP TABLE IF EXISTS usuarios;

-- ------------------------------------------------------------
-- 1. TABELA DE USUÁRIOS E PERFIS DE ACESSO
-- ------------------------------------------------------------
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil ENUM('ADMINISTRADOR', 'OPERADOR', 'TECNICO') NOT NULL,
    status ENUM('ATIVO', 'INATIVO') DEFAULT 'ATIVO',
    data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 2. TABELA DE EMBARCAÇÕES (RF01)
-- ------------------------------------------------------------
CREATE TABLE embarcacoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    capacidade_passageiros INT NOT NULL DEFAULT 0,
    capacidade_carga_ton DECIMAL(8,2) DEFAULT 0.00,
    ano_fabricacao INT NOT NULL,
    horimetro_horas INT NOT NULL DEFAULT 0,
    status ENUM('ATIVA', 'EM_MANUTENCAO', 'INATIVA') DEFAULT 'ATIVA',
    data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 3. TABELA DE TRIPULAÇÃO (RF02)
-- ------------------------------------------------------------
CREATE TABLE tripulantes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    categoria_habilitacao ENUM('PILOTO_FLUVIAL', 'CONDUTOR_FLUVIAL', 'ARRAIS_AMADOR', 'MESTRE_AMADOR', 'CAPITAO_AMADOR') NOT NULL,
    numero_registro_cir VARCHAR(30) NOT NULL UNIQUE,
    data_vencimento_cir DATE NOT NULL,
    status ENUM('DISPONIVEL', 'EM_VIAGEM', 'INATIVO') DEFAULT 'DISPONIVEL'
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 4. TABELA DE ROTAS PRÉ-CADASTRADAS
-- ------------------------------------------------------------
CREATE TABLE rotas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 5. TABELA DE DOCUMENTAÇÃO OBRIGATÓRIA (RF09)
-- ------------------------------------------------------------
CREATE TABLE documentos_embarcacao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_embarcacao INT NOT NULL,
    tipo_documento ENUM('VISTORIA_CAPITANIA', 'SEGURO_DPEM', 'LICENCA_AMBIENTAL', 'CERTIFICADO_NAVEGABILIDADE') NOT NULL,
    numero_documento VARCHAR(50) NOT NULL,
    data_emissao DATE NOT NULL,
    data_vencimento DATE NOT NULL,
    status ENUM('VALIDO', 'ALERTA_VENCIMENTO', 'VENCIDO') DEFAULT 'VALIDO',
    FOREIGN KEY (id_embarcacao) REFERENCES embarcacoes(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 6. TABELA DE REGISTRO DE VIAGENS (RF05, RN01, RN02)
-- ------------------------------------------------------------
CREATE TABLE viagens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_embarcacao INT NOT NULL,
    id_comandante INT NOT NULL,
    rota_destino VARCHAR(150) NOT NULL,
    data_hora_partida DATETIME NOT NULL,
    data_hora_chegada DATETIME NULL,
    quantidade_passageiros INT NOT NULL DEFAULT 0,
    status ENUM('EM_ANDAMENTO', 'CONCLUIDA', 'CANCELADA') DEFAULT 'EM_ANDAMENTO',
    FOREIGN KEY (id_embarcacao) REFERENCES embarcacoes(id),
    FOREIGN KEY (id_comandante) REFERENCES tripulantes(id)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 7. TABELA DE MANUTENÇÕES E AGENDAMENTOS (RF03, RF04, RF06, RF08)
-- ------------------------------------------------------------
CREATE TABLE manutencoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_embarcacao INT NOT NULL,
    tipo_manutencao ENUM('PREVENTIVA', 'CORRETIVA') NOT NULL,
    descricao_servico TEXT NOT NULL,
    horimetro_agendado INT NULL,
    data_agendamento DATE NOT NULL,
    data_execucao DATE NULL,
    custo_total DECIMAL(10,2) DEFAULT 0.00,
    status ENUM('AGENDADA', 'EM_ANDAMENTO', 'CONCLUIDA', 'CANCELADA') DEFAULT 'AGENDADA',
    FOREIGN KEY (id_embarcacao) REFERENCES embarcacoes(id)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 8. TABELA DE ABASTECIMENTOS DE COMBUSTÍVEL (RF11)
-- ------------------------------------------------------------
CREATE TABLE abastecimentos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_embarcacao INT NOT NULL,
    data_abastecimento DATETIME DEFAULT CURRENT_TIMESTAMP,
    quantidade_litros DECIMAL(8,2) NOT NULL,
    valor_total DECIMAL(10,2) NOT NULL,
    fornecedor_posto VARCHAR(100) NOT NULL,
    FOREIGN KEY (id_embarcacao) REFERENCES embarcacoes(id)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 9. TABELA DE INCIDENTES OPERACIONAIS (RF12)
-- ------------------------------------------------------------
CREATE TABLE incidentes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_embarcacao INT NOT NULL,
    id_viagem INT NULL,
    data_incidente DATETIME DEFAULT CURRENT_TIMESTAMP,
    descricao TEXT NOT NULL,
    gravidade ENUM('BAIXA', 'MEDIA', 'ALTA', 'CRITICA') NOT NULL,
    status ENUM('PENDENTE', 'EM_ANALISE', 'RESOLVIDO') DEFAULT 'PENDENTE',
    FOREIGN KEY (id_embarcacao) REFERENCES embarcacoes(id),
    FOREIGN KEY (id_viagem) REFERENCES viagens(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================
-- DADOS INICIAIS DE TESTE (POPULAÇÃO DO BANCO)
-- ============================================================

-- Usuários do Sistema
INSERT INTO usuarios (nome, email, senha, perfil) VALUES
('Vitoria Monteiro (Admin)', 'admin@nautica.com', 'admin123', 'ADMINISTRADOR'),
('Carlos Despachante', 'operador@nautica.com', 'operador123', 'OPERADOR'),
('Roberto Engenheiro', 'tecnico@nautica.com', 'tecnico123', 'TECNICO');

-- Embarcações
INSERT INTO embarcacoes (nome, modelo, capacidade_passageiros, ano_fabricacao, horimetro_horas, status) VALUES
('Titan Fluvial I', 'Catamarã 40ft', 60, 2021, 250, 'ATIVA'),
('Lobo do Mar', 'Lancha Rápida 28ft', 12, 2019, 180, 'ATIVA'),
('Nautico Prime', 'Empurrador Fluvial', 6, 2018, 420, 'EM_MANUTENCAO');

-- Tripulantes
INSERT INTO tripulantes (nome, cpf, categoria_habilitacao, numero_registro_cir, data_vencimento_cir) VALUES
('João Silva', '111.222.333-44', 'PILOTO_FLUVIAL', 'CIR-998877', '2027-12-31'),
('Marcos Souza', '555.666.777-88', 'CONDUTOR_FLUVIAL', 'CIR-112233', '2026-06-15');

-- Rotas
INSERT INTO rotas (nome) VALUES 
('Parintins'),
('Careiro Castanho'),
('Itacoatiara'),
('Tefé'),
('Coari'),
('Tabatinga');

-- Documentos
INSERT INTO documentos_embarcacao (id_embarcacao, tipo_documento, numero_documento, data_emissao, data_vencimento, status) VALUES
(1, 'VISTORIA_CAPITANIA', 'VIS-2024-001', '2024-01-10', '2026-12-31', 'VALIDO'),
(2, 'SEGURO_DPEM', 'SEG-998811', '2023-05-01', '2026-09-01', 'ALERTA_VENCIMENTO');

-- Viagens de Teste
INSERT INTO viagens (id_embarcacao, id_comandante, rota_destino, data_hora_partida, quantidade_passageiros, status) VALUES
(1, 1, 'Parintins', '2026-08-27 08:00:00', 45, 'EM_ANDAMENTO'),
(2, 2, 'Careiro Castanho', '2026-08-26 14:00:00', 10, 'CONCLUIDA');

-- Manutenções
INSERT INTO manutencoes (id_embarcacao, tipo_manutencao, descricao_servico, horimetro_agendado, data_agendamento, status) VALUES
(1, 'PREVENTIVA', 'Troca de óleo do motor principal e substituição dos filtros de combustível.', 260, '2026-09-10', 'AGENDADA'),
(3, 'CORRETIVA', 'Reparo na hélice de bombordo devido a impacto com destroço fluvial.', NULL, '2026-08-15', 'EM_ANDAMENTO');

-- Abastecimentos de Teste
INSERT INTO abastecimentos (id_embarcacao, quantidade_litros, valor_total, fornecedor_posto) VALUES
(1, 500.00, 3250.00, 'Posto Fluvial Rio Negro'),
(2, 120.50, 783.25, 'Posto Ponta Negra');

-- Incidentes de Teste
INSERT INTO incidentes (id_embarcacao, id_viagem, descricao, gravidade, status) VALUES
(1, 1, 'Pequena oscilação na pressão do óleo durante navegação.', 'BAIXA', 'PENDENTE');