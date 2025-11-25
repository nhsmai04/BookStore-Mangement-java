package org.librarymanagement.repository;

import org.librarymanagement.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author,Integer>  {
    Optional<Author> findByName(String name);
}
