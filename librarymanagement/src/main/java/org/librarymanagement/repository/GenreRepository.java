package org.librarymanagement.repository;

import org.librarymanagement.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre,Integer> {
    Optional<Genre> findByName(String name);

}
