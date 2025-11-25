package org.librarymanagement.dto.response;

import java.util.Set;

public record AuthorDetailDto(
        Integer id,
        String name,
        String bio,
        Set<BookAuthorRawDto> books
) {
}