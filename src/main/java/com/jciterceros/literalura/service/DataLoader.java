package com.jciterceros.literalura.service;

import com.jciterceros.literalura.model.Autor;
import com.jciterceros.literalura.model.Livro;
import com.jciterceros.literalura.repository.AutorRepository;
import com.jciterceros.literalura.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
@Order(1)
public class DataLoader implements CommandLineRunner {

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private AutorRepository autorRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("🌱 DataLoader iniciando...");

        // Verificar se já existem dados
        long countLivros = livroRepository.count();
        long countAutores = autorRepository.count();

        System.out.println("📊 Status atual do banco:");
        System.out.println("   - Livros: " + countLivros);
        System.out.println("   - Autores: " + countAutores);

        if (countLivros > 0 || countAutores > 0) {
            System.out.println("⚠️  Dados já existem no banco. Pulando carregamento de seeds.");
            return;
        }

        System.out.println("🚀 Carregando dados iniciais (seeds)...");

        // Criar autores
        System.out.println("👥 Criando autores...");
        // Autor machado = criarAutor("Machado de Assis", 1839, 1908);
        Autor jorge = criarAutor("Jorge Amado", 1912, 2001);
        Autor clarice = criarAutor("Clarice Lispector", 1920, 1977);
        Autor guimaraes = criarAutor("Guimarães Rosa", 1908, 1967);
        Autor drummond = criarAutor("Carlos Drummond de Andrade", 1902, 1987);
        Autor verissimo = criarAutor("Érico Veríssimo", 1905, 1975);
        Autor lobato = criarAutor("Monteiro Lobato", 1882, 1948);
        Autor alencar = criarAutor("José de Alencar", 1829, 1877);
        Autor castro = criarAutor("Euclides da Cunha", 1866, 1909);
        Autor lima = criarAutor("Lima Barreto", 1881, 1922);

        // Criar livros
        System.out.println("📚 Criando livros...");
        // criarLivro("Dom Casmurro", "pt", 1543, Arrays.asList(machado));
        // criarLivro("Memórias Póstumas de Brás Cubas", "pt", 1234,
        // Arrays.asList(machado));
        criarLivro("Gabriela, Cravo e Canela", "pt", 987, Arrays.asList(jorge));
        criarLivro("Dona Flor e Seus Dois Maridos", "pt", 876, Arrays.asList(jorge));
        criarLivro("A Hora da Estrela", "pt", 765, Arrays.asList(clarice));
        criarLivro("Grande Sertão: Veredas", "pt", 654, Arrays.asList(guimaraes));
        criarLivro("Sagarana", "pt", 543, Arrays.asList(guimaraes));
        criarLivro("Sentimento do Mundo", "pt", 432, Arrays.asList(drummond));
        criarLivro("Olhai os Lírios do Campo", "pt", 321, Arrays.asList(verissimo));
        criarLivro("O Sítio do Pica-Pau Amarelo", "pt", 210, Arrays.asList(lobato));

        System.out.println("Seeds carregados com sucesso!");
        System.out.println("Status final do banco:");
        System.out.println("   - Livros: " + livroRepository.count());
        System.out.println("   - Autores: " + autorRepository.count());
    }

    private Autor criarAutor(String nome, Integer anoNascimento, Integer anoFalecimento) {
        Autor autor = new Autor();
        autor.setNome(nome);
        autor.setAnoNascimento(anoNascimento);
        autor.setAnoFalecimento(anoFalecimento);
        return autorRepository.save(autor);
    }

    private Livro criarLivro(String titulo, String idioma, Integer downloads, List<Autor> autores) {
        Livro livro = new Livro();
        livro.setTitulo(titulo);
        livro.setIdioma(idioma);
        livro.setNumeroDownloads(downloads);
        livro.setAutores(autores);
        return livroRepository.save(livro);
    }
}