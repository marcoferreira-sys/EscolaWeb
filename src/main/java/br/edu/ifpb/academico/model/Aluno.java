package br.edu.ifpb.academico.model;

import java.util.ArrayList;
import java.util.List;

public class Aluno {

    private String matricula;
    private String nome;
    private Integer idade;
    private String rg;
    private String email;

    // Aluno (1,n) <-> Turma (1,n): ids das turmas em que o aluno está matriculado
    private List<Integer> idsTurmas = new ArrayList<>();

    public Aluno() {

    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Integer> getIdsTurmas() {
        return idsTurmas;
    }

    public void setIdsTurmas(List<Integer> idsTurmas) {
        this.idsTurmas = idsTurmas;
    }

}
