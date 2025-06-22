package com.jciterceros.literalura.controller;

import com.jciterceros.literalura.model.Livro;
import com.jciterceros.literalura.service.LivroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/livros")
public class LivroController {

    @Autowired
    private LivroService livroService;

    @GetMapping
    public ResponseEntity<List<Livro>> listarTodosLivros() {
        List<Livro> livros = livroService.buscarTodosLivros();
        return ResponseEntity.ok(livros);
    }

    @GetMapping("/top5")
    public ResponseEntity<List<Livro>> listarTop5Livros() {
        List<Livro> livros = livroService.buscarTop5Livros();
        return ResponseEntity.ok(livros);
    }

    @GetMapping("/idioma/{idioma}")
    public ResponseEntity<List<Livro>> buscarPorIdioma(@PathVariable String idioma) {
        List<Livro> livros = livroService.buscarPorIdioma(idioma);
        return ResponseEntity.ok(livros);
    }

    @GetMapping("/idiomas")
    public ResponseEntity<List<String>> listarIdiomasDisponiveis() {
        List<String> idiomas = livroService.buscarIdiomasDisponiveis();
        return ResponseEntity.ok(idiomas);
    }

    @GetMapping("/buscar")
    public ResponseEntity<Livro> buscarPorTitulo(@RequestParam String titulo) {
        Optional<Livro> livro = livroService.buscarPorTitulo(titulo);
        return livro.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}