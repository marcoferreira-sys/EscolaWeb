package br.edu.ifpb.academico.dao;

import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpb.academico.dto.TurmaDTO;
import br.edu.ifpb.academico.model.Turma;

@Repository
public class TurmaDAO {

    private final JdbcClient jdbcClient;

    public TurmaDAO(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    // Lista simples (usada, por exemplo, no formulário de Aluno)
    public List<Turma> listar() {
        return this.jdbcClient
                .sql("SELECT id_turma, serie, turno FROM turma ORDER BY serie, turno, id_turma")
                .query(Turma.class).list();
    }

    // Lista com salas, professores, matérias e quantidade de alunos de cada turma
    public List<TurmaDTO> listarComDetalhes() {
        String consulta = """
                SELECT t.id_turma, t.serie, t.turno,
                  COALESCE((SELECT string_agg('Sala ' || s.numero_sala || ' (' || s.andar_sala || 'º andar)',
                                              ', ' ORDER BY s.andar_sala, s.numero_sala)
                            FROM turma_sala ts INNER JOIN sala s ON s.id_sala = ts.id_sala
                            WHERE ts.id_turma = t.id_turma), '-') AS salas,
                  COALESCE((SELECT string_agg(p.nome, ', ' ORDER BY p.nome)
                            FROM professor_turma pt INNER JOIN professor p ON p.codigo_professor = pt.codigo_professor
                            WHERE pt.id_turma = t.id_turma), '-') AS professores,
                  COALESCE((SELECT string_agg(m.nome_materia, ', ' ORDER BY m.nome_materia)
                            FROM turma_materia tm INNER JOIN materia m ON m.id_materia = tm.id_materia
                            WHERE tm.id_turma = t.id_turma), '-') AS materias,
                  (SELECT COUNT(*) FROM aluno_turma at2 WHERE at2.id_turma = t.id_turma) AS qtd_alunos
                FROM turma t
                ORDER BY t.serie, t.turno, t.id_turma
                """;
        return this.jdbcClient.sql(consulta).query(TurmaDTO.class).list();
    }

    // Insere a turma e seus relacionamentos na mesma transação.
    // O retorno é o id gerado para a turma.
    @Transactional
    public Integer salvar(Turma turma) {
        Integer idTurma = jdbcClient
                .sql("INSERT INTO turma (serie, turno) VALUES (:serie, :turno) RETURNING id_turma")
                .param("serie", turma.getSerie())
                .param("turno", turma.getTurno())
                .query(Integer.class)
                .single();
        inserirRelacionamentos(idTurma, turma);
        return idTurma;
    }

    // o retorno inteiro corresponde ao numero de linhas afetadas na tabela turma
    @Transactional
    public Integer atualizar(Turma turma) {
        Integer linhas = jdbcClient
                .sql("UPDATE turma SET serie = :serie, turno = :turno WHERE id_turma = :id")
                .param("serie", turma.getSerie())
                .param("turno", turma.getTurno())
                .param("id", turma.getIdTurma())
                .update();
        removerRelacionamentos(turma.getIdTurma());
        inserirRelacionamentos(turma.getIdTurma(), turma);
        return linhas;
    }

    // Retorna a turma com os ids de salas, professores e matérias já preenchidos
    public Turma buscar(Integer idTurma) {
        Turma turma = jdbcClient
                .sql("SELECT id_turma, serie, turno FROM turma WHERE id_turma = :id")
                .param("id", idTurma)
                .query(Turma.class)
                .optional().orElse(null);
        if (turma != null) {
            turma.setIdsSalas(buscarIds("SELECT id_sala FROM turma_sala WHERE id_turma = :id", idTurma));
            turma.setIdsProfessores(
                    buscarIds("SELECT codigo_professor FROM professor_turma WHERE id_turma = :id", idTurma));
            turma.setIdsMaterias(buscarIds("SELECT id_materia FROM turma_materia WHERE id_turma = :id", idTurma));
        }
        return turma;
    }

    // Falha com DataIntegrityViolationException se houver alunos matriculados na turma
    public Integer excluir(Integer idTurma) {
        return jdbcClient.sql("DELETE FROM turma WHERE id_turma = :id")
                .param("id", idTurma)
                .update();
    }

    private List<Integer> buscarIds(String sql, Integer idTurma) {
        return jdbcClient.sql(sql).param("id", idTurma).query(Integer.class).list();
    }

    private void removerRelacionamentos(Integer idTurma) {
        jdbcClient.sql("DELETE FROM turma_sala WHERE id_turma = :id").param("id", idTurma).update();
        jdbcClient.sql("DELETE FROM professor_turma WHERE id_turma = :id").param("id", idTurma).update();
        jdbcClient.sql("DELETE FROM turma_materia WHERE id_turma = :id").param("id", idTurma).update();
    }

    private void inserirRelacionamentos(Integer idTurma, Turma turma) {
        for (Integer idSala : turma.getIdsSalas()) {
            jdbcClient.sql("INSERT INTO turma_sala (id_turma, id_sala) VALUES (:turma, :sala)")
                    .param("turma", idTurma).param("sala", idSala).update();
        }
        for (Integer codigoProfessor : turma.getIdsProfessores()) {
            jdbcClient.sql("INSERT INTO professor_turma (codigo_professor, id_turma) VALUES (:professor, :turma)")
                    .param("professor", codigoProfessor).param("turma", idTurma).update();
        }
        for (Integer idMateria : turma.getIdsMaterias()) {
            jdbcClient.sql("INSERT INTO turma_materia (id_turma, id_materia) VALUES (:turma, :materia)")
                    .param("turma", idTurma).param("materia", idMateria).update();
        }
    }

}
