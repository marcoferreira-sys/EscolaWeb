package br.edu.ifpb.academico.controller;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.academico.dao.AlunoDAO;
import br.edu.ifpb.academico.dao.TurmaDAO;
import br.edu.ifpb.academico.model.Aluno;

@Controller
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoDAO alunoDAO;
    private final TurmaDAO turmaDAO;

    // Mensagens de erro e sucesso para exibição ao usuário
    private static final String MENSAGEM_SUCESSO = "Operação realizada com sucesso!";
    private static final String MENSAGEM_ERRO = "Houve um erro ao realizar a operação.";
    private static final String NOME_VARIAVEL_MENSAGEM_SUCESSO = "mensagemSucesso";
    private static final String NOME_VARIAVEL_MENSAGEM_ERRO = "mensagemErro";

    public AlunoController(AlunoDAO alunoDAO, TurmaDAO turmaDAO) {
        this.alunoDAO = alunoDAO;
        this.turmaDAO = turmaDAO;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("alunos", alunoDAO.listar());
        return "listAlunos";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("aluno", new Aluno());
        model.addAttribute("turmas", turmaDAO.listar());
        return "cadastrarAluno";
    }

    @PostMapping
    public String salvar(@ModelAttribute Aluno aluno, Model model, RedirectAttributes redirectAttributes) {
        String erroValidacao = validar(aluno);
        if (erroValidacao != null) {
            model.addAttribute(NOME_VARIAVEL_MENSAGEM_ERRO, erroValidacao);
            model.addAttribute("turmas", turmaDAO.listar());
            return "cadastrarAluno";
        }
        try {
            alunoDAO.salvar(aluno);
            redirectAttributes.addFlashAttribute(NOME_VARIAVEL_MENSAGEM_SUCESSO, MENSAGEM_SUCESSO);
        } catch (DuplicateKeyException e) {
            // matrícula (PK) ou RG (UNIQUE) já cadastrados
            model.addAttribute(NOME_VARIAVEL_MENSAGEM_ERRO, "Já existe um aluno com esta matrícula ou este RG.");
            model.addAttribute("turmas", turmaDAO.listar());
            return "cadastrarAluno";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(NOME_VARIAVEL_MENSAGEM_ERRO, MENSAGEM_ERRO);
            e.printStackTrace();
        }
        return "redirect:/alunos";
    }

    @GetMapping("/editar/{matricula}")
    public String encaminhaParaEdicao(@PathVariable String matricula, Model model,
            RedirectAttributes redirectAttributes) {
        Aluno aluno = alunoDAO.buscar(matricula);
        if (aluno == null) {
            redirectAttributes.addFlashAttribute(NOME_VARIAVEL_MENSAGEM_ERRO, "Aluno não encontrado.");
            return "redirect:/alunos";
        }
        model.addAttribute("aluno", aluno);
        model.addAttribute("turmas", turmaDAO.listar());
        return "editarAluno";
    }

    @PostMapping("/atualizar")
    public String atualizar(@ModelAttribute Aluno aluno, Model model, RedirectAttributes redirectAttributes) {
        String erroValidacao = validar(aluno);
        if (erroValidacao != null) {
            model.addAttribute(NOME_VARIAVEL_MENSAGEM_ERRO, erroValidacao);
            model.addAttribute("turmas", turmaDAO.listar());
            return "editarAluno";
        }
        try {
            alunoDAO.atualizar(aluno);
            redirectAttributes.addFlashAttribute(NOME_VARIAVEL_MENSAGEM_SUCESSO, MENSAGEM_SUCESSO);
        } catch (DuplicateKeyException e) {
            model.addAttribute(NOME_VARIAVEL_MENSAGEM_ERRO, "Já existe outro aluno com este RG.");
            model.addAttribute("turmas", turmaDAO.listar());
            return "editarAluno";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(NOME_VARIAVEL_MENSAGEM_ERRO, MENSAGEM_ERRO);
            e.printStackTrace();
        }
        return "redirect:/alunos";
    }

    @GetMapping("/excluir/{matricula}")
    public String encaminhaParaExclusao(@PathVariable String matricula, Model model,
            RedirectAttributes redirectAttributes) {
        Aluno aluno = alunoDAO.buscar(matricula);
        if (aluno == null) {
            redirectAttributes.addFlashAttribute(NOME_VARIAVEL_MENSAGEM_ERRO, "Aluno não encontrado.");
            return "redirect:/alunos";
        }
        model.addAttribute("aluno", aluno);
        return "excluirAluno";
    }

    @PostMapping("/excluir")
    public String excluir(@ModelAttribute Aluno aluno, RedirectAttributes redirectAttributes) {
        try {
            alunoDAO.excluir(aluno.getMatricula());
            redirectAttributes.addFlashAttribute(NOME_VARIAVEL_MENSAGEM_SUCESSO, MENSAGEM_SUCESSO);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(NOME_VARIAVEL_MENSAGEM_ERRO, MENSAGEM_ERRO);
            e.printStackTrace();
        }
        return "redirect:/alunos";
    }

    @GetMapping("/listarAlunosComTurmas")
    public String listarAlunosComTurmas(Model model) {
        model.addAttribute("alunosComTurmas", alunoDAO.listarAlunosComTurmas());
        return "listAlunosComTurmas";
    }

    // Regra do modelo conceitual: Aluno (1,n) Turma
    private String validar(Aluno aluno) {
        if (aluno.getMatricula() == null || aluno.getMatricula().isBlank()) {
            return "Informe a matrícula.";
        }
        if (aluno.getNome() == null || aluno.getNome().isBlank()) {
            return "Informe o nome.";
        }
        if (aluno.getIdade() == null || aluno.getIdade() <= 0) {
            return "Informe uma idade válida.";
        }
        if (aluno.getRg() == null || aluno.getRg().isBlank()) {
            return "Informe o RG.";
        }
        if (aluno.getIdsTurmas().isEmpty()) {
            return "Selecione ao menos uma turma.";
        }
        return null;
    }

}
