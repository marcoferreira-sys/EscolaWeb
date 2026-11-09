package br.edu.ifpb.academico.dao;

import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import br.edu.ifpb.academico.model.Professor;

@Repository
public class ProfessorDAO {

    private final JdbcClient jdbcClient;

    public ProfessorDAO(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Professor> listar() {
        return this.jdbcClient
                .sql("SELECT codigo_professor, nome FROM professor ORDER BY nome")
                .query(Professor.class).list();
    }

}
