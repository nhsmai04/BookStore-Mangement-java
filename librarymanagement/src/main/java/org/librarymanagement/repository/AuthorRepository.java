package org.librarymanagement.repository;

import org.librarymanagement.dto.response.AuthorRaw;
import org.librarymanagement.dto.response.BookAuthorRawDto;
import org.librarymanagement.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AuthorRepository extends JpaRepository<Author,Integer>  {
    @NonNull
    Optional<Author> findByName(@NonNull String name);

    @Query("""
    SELECT new org.librarymanagement.dto.response.AuthorRaw(
        a.id,
        a.name,
        a.bio,
        COUNT(DISTINCT b.id)
    )
    FROM Author a
    LEFT JOIN a.bookAuthors ba
    LEFT JOIN ba.book b
    GROUP BY a.id, a.name
""")
    Page<AuthorRaw> findAllAuthor(Pageable pageable);
    List<Author> findAll();

    @Query("""
    SELECT new org.librarymanagement.dto.response.AuthorRaw(
        a.id,
        a.name,
        a.bio,
        COUNT(DISTINCT b.id)
    )
    FROM Author a
    LEFT JOIN a.bookAuthors ba
    LEFT JOIN ba.book b
    WHERE a.id = :id
    GROUP BY a.id, a.name
""")
    Optional<AuthorRaw> findAuthorInfoById(Integer id);

    @Query("""
    SELECT DISTINCT new org.librarymanagement.dto.response.BookAuthorRawDto(
        b.id,
        b.title,
        p.name,
        b.publishedDay,
        ba.status,
        b.totalCurrent
    )
    FROM BookAuthor ba
    JOIN ba.book b
    JOIN b.publisher p
    WHERE ba.author.id = :id
    """)
    Set<BookAuthorRawDto> findAuthorByBookId(Integer id);

}
