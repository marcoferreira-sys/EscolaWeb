package br.edu.ifpb.academico.dto;

/**
 * Aluno com as turmas em que está matriculado (resultado de JOIN entre
 * aluno, aluno_turma e turma).
 */
public class AlunoDTO {

    private String matricula;
    private String nome;
    private Integer idade;
    private String rg;
    private String email;
    private String turmas;

    public AlunoDTO() {

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

    public String getTurmas() {
        return turmas;
    }

    public void setTurmas(String turmas) {
        this.turmas = turmas;
    }

}
