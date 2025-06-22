package com.jciterceros.literalura.service;

import com.jciterceros.literalura.dto.LivroDTO;
import com.jciterceros.literalura.model.Autor;
import com.jciterceros.literalura.model.Livro;
import com.jciterceros.literalura.repository.AutorRepository;
import com.jciterceros.literalura.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LivroService {

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private ConsumoAPI consumoAPI;

    @Autowired
    private ConverteDados converteDados;

    @Transactional
    public Livro salvarLivro(LivroDTO livroDTO) {
        // Verificar se o livro já existe
        Optional<Livro> livroExistente = livroRepository.findByTituloContainingIgnoreCase(livroDTO.titulo());
        if (livroExistente.isPresent()) {
            System.out.println("Livro já existe: " + livroDTO.titulo());
            return livroExistente.get();
        }

        System.out.println("Salvando novo livro: " + livroDTO.titulo());

        // Converter DTO para entidade
        Livro livro = new Livro();
        livro.setTitulo(livroDTO.titulo());
        livro.setIdioma(livroDTO.idiomas().get(0)); // Pega o primeiro idioma
        livro.setNumeroDownloads(livroDTO.numeroDownloads());

        // Processar autores
        List<Autor> autores = livroDTO.autores().stream()
                .map(this::processarAutor)
                .collect(Collectors.toList());

        System.out.println("Autores processados: " + autores.size() + " autor(es)");

        // Associar autores ao livro
        livro.setAutores(autores);

        // Salvar livro com autores
        Livro livroSalvo = livroRepository.save(livro);
        System.out.println("Livro salvo com sucesso: " + livroSalvo.getTitulo() + " (ID: " + livroSalvo.getId() + ")");
        System.out.println("Autores associados: " + livroSalvo.getAutores().size() + " autor(es)");

        return livroSalvo;
    }

    @Transactional
    private Autor processarAutor(com.jciterceros.literalura.dto.AutorDTO autorDTO) {
        // Verificar se o autor já existe
        Optional<Autor> autorExistente = autorRepository.findByNomeContainingIgnoreCase(autorDTO.nome());
        if (autorExistente.isPresent()) {
            System.out.println("Autor encontrado: " + autorDTO.nome());
            return autorExistente.get();
        }

        // Criar novo autor
        Autor autor = new Autor();
        autor.setNome(autorDTO.nome());
        autor.setAnoNascimento(autorDTO.anoNascimento());
        autor.setAnoFalecimento(autorDTO.anoFalecimento());

        // Salvar e retornar o autor persistido
        Autor autorSalvo = autorRepository.save(autor);
        System.out.println("Novo autor salvo: " + autorSalvo.getNome() + " (ID: " + autorSalvo.getId() + ")");
        return autorSalvo;
    }

    public List<Livro> buscarTodosLivros() {
        return livroRepository.findAll();
    }

    public List<Livro> buscarTop5Livros() {
        return livroRepository.findTop5ByOrderByNumeroDownloadsDesc();
    }

    public List<Livro> buscarPorIdioma(String idioma) {
        return livroRepository.buscarPorIdioma(idioma);
    }

    public List<String> buscarIdiomasDisponiveis() {
        return livroRepository.buscarIdiomasDisponiveis();
    }

    public Optional<Livro> buscarPorTitulo(String titulo) {
        return livroRepository.findByTituloContainingIgnoreCase(titulo);
    }

    public List<Autor> buscarAutoresVivosEmAno(Integer ano) {
        return autorRepository.buscarAutoresVivosEmAno(ano);
    }

    public List<Autor> buscarAutoresNascidosEmAno(Integer ano) {
        return autorRepository.buscarAutoresNascidosEmAno(ano);
    }

    public List<Autor> buscarAutoresPorAnoMorte(Integer ano) {
        return autorRepository.buscarAutoresPorAnoMorte(ano);
    }

    public List<Autor> buscarTodosAutores() {
        return autorRepository.findAll();
    }
}