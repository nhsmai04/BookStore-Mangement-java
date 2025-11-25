package org.librarymanagement.repository;

import org.librarymanagement.entity.Book;
import org.librarymanagement.entity.Review;
import org.librarymanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    Optional<Review> findByBookAndUser(Book book, User user);
}
