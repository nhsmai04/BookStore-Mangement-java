package org.librarymanagement.dto.response;

import java.time.LocalDate;

public record BookAuthorRawDto(
        Integer id,
        String bookTitle,
        String publisherName,
        LocalDate publishedDate,
        Integer role,
        Integer totalCurrent
) {
}
