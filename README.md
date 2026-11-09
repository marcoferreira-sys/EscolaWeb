# Escola Web

Aplicação web do projeto final da disciplina de **Banco de Dados I** do curso de Tecnologia em Sistemas para Internet (TSI) do **IFPB – Campus Guarabira**.

O domínio escolhido foi uma **escola**. A aplicação foi desenvolvida a partir do modelo `academico-web`, disponibilizado pelo professor, e adaptada ao modelo conceitual do grupo.

## Integrantes

- [Marco Antônio Ferreira Da Silva]
- [Walther Daniel Costa Ferreira Rufino]

## Vídeo de demonstração

(https://youtu.be/D4rFB6JztAQ?si=8WDKxRoC7Uj_5GcM)

## Funcionalidades

A aplicação possui **dois CRUDs, sendo um dependente do outro**.

### 1. CRUD de Turma (`/turmas`)
- Cadastrar, listar, editar e excluir turmas (série e turno).
- Cada turma é associada a **salas**, **professores** e **matérias** já cadastrados.
- Regras: toda turma precisa de pelo menos **uma sala** e **um professor** (cardinalidade (1,n) do modelo).
- A listagem mostra salas, professores, matérias e a quantidade de alunos de cada turma.
- Uma turma que tenha alunos matriculados **não pode ser excluída**.

### 2. CRUD de Aluno (`/alunos`)
- Cadastrar, listar, editar e excluir alunos (matrícula, nome, idade, RG e e-mail).
- Cada aluno é associado a **uma ou mais turmas** previamente cadastradas.
- **Dependência:** só é possível cadastrar um aluno se já existir pelo menos uma turma.
- Tela extra: **Alunos com turmas** (`/alunos/listarAlunosComTurmas`), resultado de um `JOIN` entre `aluno`, `aluno_turma` e `turma`.
- A matrícula é a chave primária e não pode ser alterada na edição.

> Salas, professores, matérias, funções e funcionários não possuem tela de cadastro. Eles são inseridos como dados iniciais pelo script `banco.sql`.

## Tecnologias

- Java 25
- Spring Boot (Spring Web MVC e Spring JDBC com `JdbcClient`)
- Thymeleaf
- PostgreSQL
- Maven

## Pré-requisitos

- **JDK 21** (não basta o JRE; é preciso o JDK para compilar). Confira com `javac -version`.
- **PostgreSQL** instalado e em execução.

## Como executar

1. **Crie o banco de dados** no PostgreSQL:
   ```sql
   CREATE DATABASE escola_web;
   ```
2. **Execute o script `banco.sql`** dentro do banco `escola_web`. Ele cria todas as tabelas e insere os dados iniciais. Pode ser feito pelo pgAdmin (Query Tool) ou pelo terminal:
   ```bash
   psql -U postgres -d escola_web -f banco.sql
   ```
3. **Configure a conexão** no arquivo `application.properties`, que fica na **raiz do projeto**, ao lado do `pom.xml`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/escola_web
   spring.datasource.username=postgres
   spring.datasource.password=SUA_SENHA
   ```
4. **Inicie a aplicação**, de uma destas formas:
   - Pelo terminal, na pasta do projeto:
     ```bash
     ./mvnw spring-boot:run        # Linux/macOS
     .\mvnw.cmd spring-boot:run    # Windows
     ```
   - Ou pelo VS Code/IDE, executando a classe `DemoApplication`.
5. **Acesse** [http://localhost:8080](http://localhost:8080).

> Não abra os arquivos `.html` direto no navegador. As páginas dependem do Thymeleaf e só funcionam pelo servidor.

> Se o `mvnw` reclamar da falta de `.mvn/wrapper/maven-wrapper.properties`, crie o arquivo com `distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip`, ou rode a aplicação pela IDE.

## Banco de dados

O script de criação do banco e das tabelas é o arquivo [`banco.sql`](banco.sql).


O modelo está normalizado. Todos os relacionamentos do modelo conceitual são muitos-para-muitos e viraram tabelas associativas. O telefone do funcionário, por ser multivalorado, virou tabela própria.

| Tabela | Origem no modelo conceitual |
|---|---|
| `funcao`, `funcionario` | Função (1,1) — Funcionário (0,n) |
| `funcionario_telefone` | Telefone (1,n) de Funcionário (atributo multivalorado) |
| `sala`, `professor`, `materia`, `turma`, `aluno` | Entidades |
| `aluno_turma` | Aluno (1,n) — Turma (1,n) |
| `turma_sala` | Sala (1,n) — Turma (1,n) |
| `professor_turma` | Professor (1,n) — Turma (1,n) |
| `professor_materia` | Matéria (1,n) — Professor (0,n) |
| `turma_materia` | Matéria (1,n) — Turma (0,n) |

Regras de integridade:
- Excluir um **aluno** remove automaticamente suas matrículas (`ON DELETE CASCADE` em `aluno_turma`).
- Excluir uma **turma com alunos** é bloqueado pelo banco (chave estrangeira sem cascata).
- Excluir uma turma sem alunos remove também suas ligações com salas, professores e matérias.

## Estrutura do projeto

```
academico-web/
├── banco.sql                     # script de criação do banco, tabelas e dados iniciais
├── application.properties        # configuração da conexão com o PostgreSQL
├── pom.xml
├── docs/                         # diagrama do banco (png, svg, dot)
└── src/main/
    ├── java/br/edu/ifpb/academico/
    │   ├── DemoApplication.java
    │   ├── model/                # Aluno, Turma, Sala, Professor, Materia
    │   ├── dto/                  # AlunoDTO, TurmaDTO (resultados de JOIN)
    │   ├── dao/                  # AlunoDAO, TurmaDAO, SalaDAO, ProfessorDAO, MateriaDAO
    │   └── controller/           # AlunoController, TurmaController
    └── resources/
        ├── static/css/           # estilos
        └── templates/            # páginas Thymeleaf
```

## Camada de acesso a dados (DAO)

Os DAOs usam o `JdbcClient` do Spring com SQL escrito à mão.

| Método | `AlunoDAO` | `TurmaDAO` |
|---|---|---|
| `listar` | lista os alunos | lista as turmas (versão simples) |
| `salvar` | `INSERT` em `aluno` e em `aluno_turma` (transação) | `INSERT ... RETURNING id_turma` e depois nas tabelas de ligação (transação) |
| `atualizar` | `UPDATE` do aluno e recriação das matrículas (transação) | `UPDATE` da turma e recriação das ligações (transação) |
| `buscar` | busca por matrícula, já com os ids das turmas | busca por id, já com os ids de salas, professores e matérias |
| `excluir` | `DELETE` por matrícula | `DELETE` por id (falha se houver alunos) |

## Rotas

| Rota | Descrição |
|---|---|
| `GET /` | página inicial |
| `GET /turmas`, `/turmas/novo` | listar e cadastrar turmas |
| `GET /turmas/editar/{id}`, `/turmas/excluir/{id}` | telas de edição e exclusão |
| `POST /turmas`, `/turmas/atualizar`, `/turmas/excluir` | salvar, atualizar e excluir |
| `GET /alunos`, `/alunos/novo` | listar e cadastrar alunos |
| `GET /alunos/editar/{matricula}`, `/alunos/excluir/{matricula}` | telas de edição e exclusão |
| `GET /alunos/listarAlunosComTurmas` | alunos com suas turmas |
| `POST /alunos`, `/alunos/atualizar`, `/alunos/excluir` | salvar, atualizar e excluir |
