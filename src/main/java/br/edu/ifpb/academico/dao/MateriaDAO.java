package br.edu.ifpb.academico.dao;

import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import br.edu.ifpb.academico.model.Materia;

@Repository
public class MateriaDAO {

    private final JdbcClient jdbcClient;

    public MateriaDAO(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Materia> listar() {
        return this.jdbcClient
                .sql("SELECT id_materia, nome_materia FROM materia ORDER BY nome_materia")
                .query(Materia.class).list();
    }

}
