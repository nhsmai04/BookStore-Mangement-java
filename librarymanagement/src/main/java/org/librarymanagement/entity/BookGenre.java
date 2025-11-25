package org.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "book_genres")
@Data
public class BookGenre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    private Integer status;
}
