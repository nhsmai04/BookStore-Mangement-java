package org.librarymanagement.dto.response;

public record AuthorRaw(
        Integer id,
        String name,
        String bio,
        Long countBook
   ) {}
