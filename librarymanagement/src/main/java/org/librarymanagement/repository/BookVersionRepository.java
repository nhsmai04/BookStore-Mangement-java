package org.librarymanagement.repository;

import jakarta.persistence.LockModeType;
import org.librarymanagement.entity.Book;
import org.librarymanagement.entity.BookVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookVersionRepository extends JpaRepository<BookVersion,Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT bv FROM BookVersion bv WHERE bv.book.id = :bookId AND bv.status = :status")
    List<BookVersion> findAvailableBooksByBookIds(Integer bookId, @Param("status") int status);

}
