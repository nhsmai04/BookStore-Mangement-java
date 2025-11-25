package org.librarymanagement.dto.response;

import java.util.Set;

public record AuthorListDto(
        Integer id,
        String name,
        Integer countBook
) {
}
