package br.edu.ifpb.academico.controller;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.academico.dao.MateriaDAO;
import br.edu.ifpb.academico.dao.ProfessorDAO;
import br.edu.ifpb.academico.dao.SalaDAO;
import br.edu.ifpb.academico.dao.TurmaDAO;
import br.edu.ifpb.academico.model.Turma;

@Controller
@RequestMapping("/turmas")
public class TurmaController {

    private final TurmaDAO turmaDAO;
    private final SalaDAO salaDAO;
    private final ProfessorDAO professorDAO;
    private final MateriaDAO materiaDAO;

    // Mensagens de erro e sucesso para exibição ao usuário
    private static final String MENSAGEM_SUCESSO = "Operação realizada com sucesso!";
    private static final String MENSAGEM_ERRO = "Houve um erro ao realizar a operação.";
    private static final String NOME_VARIAVEL_MENSAGEM_SUCESSO = "mensagemSucesso";
    private static final String NOME_VARIAVEL_MENSAGEM_ERRO = "mensagemErro";

    public TurmaController(TurmaDAO turmaDAO, SalaDAO salaDAO, ProfessorDAO professorDAO, MateriaDAO materiaDAO) {
        this.turmaDAO = turmaDAO;
        this.salaDAO = salaDAO;
        this.professorDAO = professorDAO;
        this.materiaDAO = materiaDAO;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("turmas", turmaDAO.listarComDetalhes());
        return "listTurmas";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("turma", new Turma());
        carregarOpcoes(model);
        return "cadastrarTurma";
    }

    @PostMapping
    public String salvar(@ModelAttribute Turma turma, Model model, RedirectAttributes redirectAttributes) {
        String erroValidacao = validar(turma);
        if (erroValidacao != null) {
            model.addAttribute(NOME_VARIAVEL_MENSAGEM_ERRO, erroValidacao);
            carregarOpcoes(model);
            return "cadastrarTurma";
        }
        try {
            turmaDAO.salvar(turma);
            redirectAttributes.addFlashAttribute(NOME_VARIAVEL_MENSAGEM_SUCESSO, MENSAGEM_SUCESSO);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(NOME_VARIAVEL_MENSAGEM_ERRO, MENSAGEM_ERRO);
            e.printStackTrace();
        }
        return "redirect:/turmas";
    }

    @GetMapping("/editar/{id}")
    public String encaminhaParaEdicao(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Turma turma = turmaDAO.buscar(id);
        if (turma == null) {
            redirectAttributes.addFlashAttribute(NOME_VARIAVEL_MENSAGEM_ERRO, "Turma não encontrada.");
            return "redirect:/turmas";
        }
        model.addAttribute("turma", turma);
        carregarOpcoes(model);
        return "editarTurma";
    }

    @PostMapping("/atualizar")
    public String atualizar(@ModelAttribute Turma turma, Model model, RedirectAttributes redirectAttributes) {
        String erroValidacao = validar(turma);
        if (erroValidacao != null) {
            model.addAttribute(NOME_VARIAVEL_MENSAGEM_ERRO, erroValidacao);
            carregarOpcoes(model);
            return "editarTurma";
        }
        try {
            turmaDAO.atualizar(turma);
            redirectAttributes.addFlashAttribute(NOME_VARIAVEL_MENSAGEM_SUCESSO, MENSAGEM_SUCESSO);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(NOME_VARIAVEL_MENSAGEM_ERRO, MENSAGEM_ERRO);
            e.printStackTrace();
        }
        return "redirect:/turmas";
    }

    @GetMapping("/excluir/{id}")
    public String encaminhaParaExclusao(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Turma turma = turmaDAO.buscar(id);
        if (turma == null) {
            redirectAttributes.addFlashAttribute(NOME_VARIAVEL_MENSAGEM_ERRO, "Turma não encontrada.");
            return "redirect:/turmas";
        }
        model.addAttribute("turma", turma);
        return "excluirTurma";
    }

    @PostMapping("/excluir")
    public String excluir(@ModelAttribute Turma turma, RedirectAttributes redirectAttributes) {
        try {
            turmaDAO.excluir(turma.getIdTurma());
            redirectAttributes.addFlashAttribute(NOME_VARIAVEL_MENSAGEM_SUCESSO, MENSAGEM_SUCESSO);
        } catch (DataIntegrityViolationException e) {
            // aluno_turma referencia a turma: não pode excluir turma com alunos matriculados
            redirectAttributes.addFlashAttribute(NOME_VARIAVEL_MENSAGEM_ERRO,
                    "Não é possível excluir a turma: existem alunos matriculados nela.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(NOME_VARIAVEL_MENSAGEM_ERRO, MENSAGEM_ERRO);
            e.printStackTrace();
        }
        return "redirect:/turmas";
    }

    // Regras do modelo conceitual: Turma (1,n) Sala e Turma (1,n) Professor
    private String validar(Turma turma) {
        if (turma.getSerie() == null || turma.getSerie().isBlank()) {
            return "Informe a série da turma.";
        }
        if (turma.getTurno() == null || turma.getTurno().isBlank()) {
            return "Selecione o turno da turma.";
        }
        if (turma.getIdsSalas().isEmpty()) {
            return "Selecione ao menos uma sala.";
        }
        if (turma.getIdsProfessores().isEmpty()) {
            return "Selecione ao menos um professor.";
        }
        return null;
    }

    private void carregarOpcoes(Model model) {
        model.addAttribute("salas", salaDAO.listar());
        model.addAttribute("professores", professorDAO.listar());
        model.addAttribute("materias", materiaDAO.listar());
        model.addAttribute("turnos", List.of("Manhã", "Tarde", "Noite"));
    }

}
