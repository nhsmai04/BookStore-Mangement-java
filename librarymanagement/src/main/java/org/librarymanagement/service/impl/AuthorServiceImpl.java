package org.librarymanagement.service.impl;

import org.librarymanagement.entity.Author;
import org.librarymanagement.repository.AuthorRepository;
import org.librarymanagement.service.AuthorService;
import org.librarymanagement.service.SlugService;
import org.springframework.stereotype.Service;

@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final SlugService slugService;

    public AuthorServiceImpl(AuthorRepository authorRepository,SlugService slugService) {
        this.authorRepository = authorRepository;
        this.slugService = slugService;
    }

    public Author findOrCreateAuthor(String authorName) {
        return authorRepository.findByName(authorName)
                .orElseGet(() -> {
                    Author a = new Author();
                    a.setSlug(slugService.generateUniqueSlug(authorName));
                    a.setName(authorName);
                    return authorRepository.save(a);
                });
    }
}
