package org.librarymanagement.dto.response;

import java.time.LocalDate;

public record BookSearchFlatDto(
        Integer id,
        String image,
        String title,
        String description,
        LocalDate publishedDay,
        String publisherName,
        String bookAuthor,
        String bookGenre
){}
