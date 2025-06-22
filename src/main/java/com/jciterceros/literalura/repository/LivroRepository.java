package com.jciterceros.literalura.repository;

import com.jciterceros.literalura.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {

    Optional<Livro> findByTituloContainingIgnoreCase(String titulo);

    List<Livro> findTop5ByOrderByNumeroDownloadsDesc();

    @Query("SELECT l FROM Livro l WHERE l.idioma ILIKE %:idioma%")
    List<Livro> buscarPorIdioma(String idioma);

    @Query("SELECT DISTINCT l.idioma FROM Livro l")
    List<String> buscarIdiomasDisponiveis();
}