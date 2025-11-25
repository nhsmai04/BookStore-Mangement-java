package org.librarymanagement.service;

import org.librarymanagement.dto.request.LoginUserDto;
import org.librarymanagement.dto.response.LoginResponseDto;
import org.librarymanagement.entity.Author;

public interface AuthorService {
    public Author findOrCreateAuthor(String authorName);
}
