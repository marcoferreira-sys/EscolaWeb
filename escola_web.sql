-- ============================================================
-- Banco de dados: escola_web  (Projeto Escola - Banco de Dados I)
-- Modelo físico (PostgreSQL) derivado do modelo conceitual
-- ESCOLA_ATUALIZADA, já normalizado (1FN, 2FN e 3FN):
--   * Telefone de Funcionário é multivalorado  -> tabela própria
--   * Relacionamentos N:N                      -> tabelas associativas
-- ============================================================



DROP TABLE IF EXISTS turma_materia, professor_turma, professor_materia,
                     turma_sala, aluno_turma, funcionario_telefone,
                     funcionario, funcao, aluno, turma, materia,
                     professor, sala CASCADE;

-- -------------------- ENTIDADES -----------------------------

CREATE TABLE funcao (
    id_funcao   SERIAL PRIMARY KEY,
    nome_funcao VARCHAR(40) NOT NULL UNIQUE
);

CREATE TABLE funcionario (
    cpf       CHAR(11)      PRIMARY KEY,
    nome      VARCHAR(60)   NOT NULL,
    salario   NUMERIC(10,2) NOT NULL CHECK (salario >= 0),
    id_funcao INTEGER       NOT NULL,         
    FOREIGN KEY (id_funcao) REFERENCES funcao (id_funcao)
);


CREATE TABLE funcionario_telefone (
    cpf      CHAR(11)    NOT NULL,
    telefone VARCHAR(15) NOT NULL,
    PRIMARY KEY (cpf, telefone),
    FOREIGN KEY (cpf) REFERENCES funcionario (cpf) ON DELETE CASCADE
);

CREATE TABLE sala (
    id_sala      SERIAL  PRIMARY KEY,
    numero_sala  INTEGER NOT NULL,
    andar_sala   INTEGER NOT NULL,
    UNIQUE (numero_sala, andar_sala)
);

CREATE TABLE professor (
    codigo_professor SERIAL        PRIMARY KEY,
    nome             VARCHAR(60)   NOT NULL,
    salario          NUMERIC(10,2) NOT NULL CHECK (salario >= 0),
    telefone         VARCHAR(15),
    rua              VARCHAR(60),   
    bairro           VARCHAR(40)
);

CREATE TABLE materia (
    id_materia   SERIAL      PRIMARY KEY,
    nome_materia VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE turma (
    id_turma SERIAL      PRIMARY KEY,
    serie    VARCHAR(20) NOT NULL,
    turno    VARCHAR(15) NOT NULL CHECK (turno IN ('Manhã', 'Tarde', 'Noite'))
);

CREATE TABLE aluno (
    matricula VARCHAR(15) PRIMARY KEY,
    nome      VARCHAR(60) NOT NULL,
    idade     SMALLINT    NOT NULL CHECK (idade > 0),
    rg        VARCHAR(20) NOT NULL UNIQUE,
    email     VARCHAR(60)
);

-- -------------- TABELAS ASSOCIATIVAS (N:N) -------------------


CREATE TABLE aluno_turma (
    matricula VARCHAR(15) NOT NULL,
    id_turma  INTEGER     NOT NULL,
    PRIMARY KEY (matricula, id_turma),
    FOREIGN KEY (matricula) REFERENCES aluno (matricula) ON DELETE CASCADE,
    FOREIGN KEY (id_turma)  REFERENCES turma (id_turma)            


CREATE TABLE turma_sala (
    id_turma INTEGER NOT NULL,
    id_sala  INTEGER NOT NULL,
    PRIMARY KEY (id_turma, id_sala),
    FOREIGN KEY (id_turma) REFERENCES turma (id_turma) ON DELETE CASCADE,
    FOREIGN KEY (id_sala)  REFERENCES sala (id_sala)
);


CREATE TABLE professor_turma (
    codigo_professor INTEGER NOT NULL,
    id_turma         INTEGER NOT NULL,
    PRIMARY KEY (codigo_professor, id_turma),
    FOREIGN KEY (codigo_professor) REFERENCES professor (codigo_professor),
    FOREIGN KEY (id_turma)         REFERENCES turma (id_turma) ON DELETE CASCADE
);


CREATE TABLE professor_materia (
    codigo_professor INTEGER NOT NULL,
    id_materia       INTEGER NOT NULL,
    PRIMARY KEY (codigo_professor, id_materia),
    FOREIGN KEY (codigo_professor) REFERENCES professor (codigo_professor) ON DELETE CASCADE,
    FOREIGN KEY (id_materia)       REFERENCES materia (id_materia)
);


CREATE TABLE turma_materia (
    id_turma   INTEGER NOT NULL,
    id_materia INTEGER NOT NULL,
    PRIMARY KEY (id_turma, id_materia),
    FOREIGN KEY (id_turma)   REFERENCES turma (id_turma) ON DELETE CASCADE,
    FOREIGN KEY (id_materia) REFERENCES materia (id_materia)
);

-- -------------------- DADOS INICIAIS -------------------------
-- Salas, professores e matérias ficam pré-cadastrados para que
-- o CRUD de Turma possa associá-los. Turmas e alunos são criados
-- pela aplicação (primeiro Turma, depois Aluno).

INSERT INTO funcao (nome_funcao) VALUES
('Diretor'), ('Secretário'), ('Coordenador'), ('Porteiro');

INSERT INTO funcionario (cpf, nome, salario, id_funcao) VALUES
('11111111111', 'Funcionário Exemplo 1', 6500.00, 1),
('22222222222', 'Funcionário Exemplo 2', 2800.00, 2),
('33333333333', 'Funcionário Exemplo 3', 2100.00, 4);

INSERT INTO funcionario_telefone (cpf, telefone) VALUES
('11111111111', '83900000001'),
('11111111111', '83900000002'),
('22222222222', '83900000003'),
('33333333333', '83900000004');

INSERT INTO sala (numero_sala, andar_sala) VALUES
(101, 1), (102, 1), (201, 2), (202, 2);

INSERT INTO materia (nome_materia) VALUES
('Português'), ('Matemática'), ('História'), ('Geografia'), ('Ciências');

INSERT INTO professor (nome, salario, telefone, rua, bairro) VALUES
('Professor Exemplo 1', 4200.00, '83911111111', 'Rua das Flores', 'Centro'),
('Professor Exemplo 2', 4200.00, '83922222222', 'Rua do Sol', 'Nordeste'),
('Professor Exemplo 3', 4500.00, '83933333333', 'Av. Principal', 'Centro');

INSERT INTO professor_materia (codigo_professor, id_materia) VALUES
(1, 1), (2, 2), (2, 5), (3, 3), (3, 4);