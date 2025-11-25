package org.librarymanagement.dto.response;

import java.time.LocalDate;
import java.util.Set;

public record BookResponseDto(
        Integer id,
        String image,
        String title,
        String description,
        LocalDate publishedDay,
        String publisherName,
        Set<String> bookAuthor,
        Set<String> bookGenre
) {}
