package br.edu.ifpb.academico.dto;

/**
 * Turma com as informações dos seus relacionamentos já agregadas em texto
 * (salas, professores, matérias) e a quantidade de alunos matriculados.
 */
public class TurmaDTO {

    private Integer idTurma;
    private String serie;
    private String turno;
    private String salas;
    private String professores;
    private String materias;
    private Integer qtdAlunos;

    public TurmaDTO() {

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

    public String getSalas() {
        return salas;
    }

    public void setSalas(String salas) {
        this.salas = salas;
    }

    public String getProfessores() {
        return professores;
    }

    public void setProfessores(String professores) {
        this.professores = professores;
    }

    public String getMaterias() {
        return materias;
    }

    public void setMaterias(String materias) {
        this.materias = materias;
    }

    public Integer getQtdAlunos() {
        return qtdAlunos;
    }

    public void setQtdAlunos(Integer qtdAlunos) {
        this.qtdAlunos = qtdAlunos;
    }

}
