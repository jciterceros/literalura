package com.jciterceros.literalura.repository;

import com.jciterceros.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {

    Optional<Autor> findByNomeContainingIgnoreCase(String nome);

    @Query("SELECT a FROM Autor a WHERE a.anoFalecimento IS NULL")
    List<Autor> buscarAutoresVivos();

    @Query("SELECT a FROM Autor a WHERE a.anoNascimento >= :ano")
    List<Autor> buscarAutoresPorAnoNascimento(Integer ano);

    @Query("SELECT a FROM Autor a WHERE a.anoNascimento <= :ano AND (a.anoFalecimento IS NULL OR a.anoFalecimento >= :ano)")
    List<Autor> buscarAutoresVivosEmAno(Integer ano);

    @Query("SELECT a FROM Autor a WHERE a.anoNascimento = :ano")
    List<Autor> buscarAutoresNascidosEmAno(Integer ano);

    @Query("SELECT a FROM Autor a WHERE a.anoFalecimento = :ano")
    List<Autor> buscarAutoresPorAnoMorte(Integer ano);
}