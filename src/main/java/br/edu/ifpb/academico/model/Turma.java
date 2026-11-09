package br.edu.ifpb.academico.model;

import java.util.ArrayList;
import java.util.List;

public class Turma {

    private Integer idTurma;
    private String serie;
    private String turno;

    // Relacionamentos N:N da turma (ids selecionados no formulário)
    private List<Integer> idsSalas = new ArrayList<>();
    private List<Integer> idsProfessores = new ArrayList<>();
    private List<Integer> idsMaterias = new ArrayList<>();

    public Turma() {

    }

    public Integer getIdTurma() {
        return idTurma;
    }

    public void setIdTurma(Integer idTurma) {
        this.idTurma = idTurma;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public List<Integer> getIdsSalas() {
        return idsSalas;
    }

    public void setIdsSalas(List<Integer> idsSalas) {
        this.idsSalas = idsSalas;
    }

    public List<Integer> getIdsProfessores() {
        return idsProfessores;
    }

    public void setIdsProfessores(List<Integer> idsProfessores) {
        this.idsProfessores = idsProfessores;
    }

    public List<Integer> getIdsMaterias() {
        return idsMaterias;
    }

    public void setIdsMaterias(List<Integer> idsMaterias) {
        this.idsMaterias = idsMaterias;
    }

}
