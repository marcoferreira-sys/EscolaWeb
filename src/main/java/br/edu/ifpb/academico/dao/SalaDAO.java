package br.edu.ifpb.academico.dao;

import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import br.edu.ifpb.academico.model.Sala;

@Repository
public class SalaDAO {

    private final JdbcClient jdbcClient;

    public SalaDAO(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Sala> listar() {
        return this.jdbcClient
                .sql("SELECT id_sala, numero_sala, andar_sala FROM sala ORDER BY andar_sala, numero_sala")
                .query(Sala.class).list();
    }

}
