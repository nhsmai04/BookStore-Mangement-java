package org.librarymanagement.repository;

import org.librarymanagement.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PublisherRepository extends JpaRepository<Publisher, Integer> {
    Optional<Publisher> findByName(String name);
    Optional<Publisher> findBySlug(String slug);
    @Query("select p.slug from Publisher p")
    List<String> findAllSlugs();

}
