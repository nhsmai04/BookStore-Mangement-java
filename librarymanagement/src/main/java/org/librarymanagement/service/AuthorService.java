package org.librarymanagement.service;

import org.librarymanagement.dto.response.AuthorDetailDto;
import org.librarymanagement.dto.response.AuthorListDto;
import org.librarymanagement.dto.response.AuthorRaw;
import org.librarymanagement.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface AuthorService {
    Author findOrCreateAuthor(String authorName);
    Page<AuthorListDto> getAllAuthors(Pageable pageable);
    List<Author> getAllAuthorsRaw();
    AuthorDetailDto getAuthorDetailById(Integer id);
    Author findOrCreateAuthorInMemory(
            String name,
            Set<String> usedSlugs
    );
}
