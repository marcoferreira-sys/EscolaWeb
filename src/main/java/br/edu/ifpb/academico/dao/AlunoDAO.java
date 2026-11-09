package br.edu.ifpb.academico.dao;

import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpb.academico.dto.AlunoDTO;
import br.edu.ifpb.academico.model.Aluno;

@Repository
public class AlunoDAO {

    private final JdbcClient jdbcClient;

    public AlunoDAO(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Aluno> listar() {
        return this.jdbcClient
                .sql("SELECT matricula, nome, idade, rg, email FROM aluno ORDER BY nome")
                .query(Aluno.class).list();
    }

    // Insere o aluno e suas matrículas em turmas na mesma transação.
    // o retorno inteiro corresponde ao numero de linhas afetadas na tabela aluno
    @Transactional
    public Integer salvar(Aluno aluno) {
        Integer linhas = jdbcClient
                .sql("INSERT INTO aluno (matricula, nome, idade, rg, email) "
                        + "VALUES (:matricula, :nome, :idade, :rg, :email)")
                .param("matricula", aluno.getMatricula())
                .param("nome", aluno.getNome())
                .param("idade", aluno.getIdade())
                .param("rg", aluno.getRg())
                .param("email", aluno.getEmail())
                .update();
        inserirTurmas(aluno);
        return linhas;
    }

    // o retorno inteiro corresponde ao numero de linhas afetadas na tabela aluno
    @Transactional
    public Integer atualizar(Aluno aluno) {
        Integer linhas = jdbcClient
                .sql("UPDATE aluno SET nome = :nome, idade = :idade, rg = :rg, email = :email "
                        + "WHERE matricula = :matricula")
                .param("nome", aluno.getNome())
                .param("idade", aluno.getIdade())
                .param("rg", aluno.getRg())
                .param("email", aluno.getEmail())
                .param("matricula", aluno.getMatricula())
                .update();
        jdbcClient.sql("DELETE FROM aluno_turma WHERE matricula = :matricula")
                .param("matricula", aluno.getMatricula())
                .update();
        inserirTurmas(aluno);
        return linhas;
    }

    // Retorna o aluno com os ids das turmas já preenchidos (null se não existir)
    public Aluno buscar(String matricula) {
        Aluno aluno = jdbcClient
                .sql("SELECT matricula, nome, idade, rg, email FROM aluno WHERE matricula = :matricula")
                .param("matricula", matricula)
                .query(Aluno.class)
                .optional().orElse(null);
        if (aluno != null) {
            aluno.setIdsTurmas(jdbcClient
                    .sql("SELECT id_turma FROM aluno_turma WHERE matricula = :matricula")
                    .param("matricula", matricula)
                    .query(Integer.class).list());
        }
        return aluno;
    }

    // As linhas de aluno_turma são removidas automaticamente (ON DELETE CASCADE)
    public Integer excluir(String matricula) {
        return jdbcClient.sql("DELETE FROM aluno WHERE matricula = :matricula")
                .param("matricula", matricula)
                .update();
    }

    public List<AlunoDTO> listarAlunosComTurmas() {
        String consulta = """
                SELECT a.matricula, a.nome, a.idade, a.rg, a.email,
                       string_agg('Turma ' || t.id_turma || ' - ' || t.serie || ' (' || t.turno || ')',
                                  ', ' ORDER BY t.serie, t.turno, t.id_turma) AS turmas
                FROM aluno a
                INNER JOIN aluno_turma atu ON atu.matricula = a.matricula
                INNER JOIN turma t ON t.id_turma = atu.id_turma
                GROUP BY a.matricula, a.nome, a.idade, a.rg, a.email
                ORDER BY a.nome
                """;
        return this.jdbcClient.sql(consulta).query(AlunoDTO.class).list();
    }

    private void inserirTurmas(Aluno aluno) {
        for (Integer idTurma : aluno.getIdsTurmas()) {
            jdbcClient.sql("INSERT INTO aluno_turma (matricula, id_turma) VALUES (:matricula, :turma)")
                    .param("matricula", aluno.getMatricula())
                    .param("turma", idTurma)
                    .update();
        }
    }

}
