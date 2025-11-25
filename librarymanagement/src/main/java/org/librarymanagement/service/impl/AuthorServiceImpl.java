package org.librarymanagement.service.impl;

import org.librarymanagement.dto.response.*;
import org.librarymanagement.entity.Author;
import org.librarymanagement.exception.NotFoundException;
import org.librarymanagement.repository.AuthorRepository;
import org.librarymanagement.service.AuthorService;
import org.librarymanagement.service.SlugService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import java.util.*;
import java.util.stream.Collectors;

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
                    a.setName(authorName);
                    a.setSlug(slugService.generateUniqueSlug(authorName));
                    return authorRepository.save(a);
                });
    }
    public Author findOrCreateAuthorInMemory(
            String name,
            Set<String> usedSlugs
    ){
        return authorRepository.findByName(name)
                .orElseGet(() -> {
                    Author a = new Author();
                    a.setName(name);
                    a.setSlug(
                            slugService.generateUniqueSlugInMemory(name, usedSlugs)
                    );
                    return authorRepository.save(a);
                });
    }

    public Page<AuthorListDto> getAllAuthors(Pageable pageable) {

        Page<AuthorRaw> rawPage = authorRepository.findAllAuthor(pageable);

        List<AuthorListDto> authors = rawPage.stream()
                .map(raw -> new AuthorListDto(
                        raw.id(),
                        raw.name(),
                        raw.countBook().intValue()
                ))
                .toList();

        return new PageImpl<>(
                authors,
                pageable,
                rawPage.getTotalElements() // nhớ count(distinct author.id)
        );
    }

    public List<Author> getAllAuthorsRaw()
    {
        return authorRepository.findAll();
    }
    public AuthorDetailDto getAuthorDetailById(Integer id)
    {
        AuthorRaw raw = authorRepository.findAuthorInfoById(id)
                .orElseThrow(() -> new NotFoundException("Author not found with id: " + id));
        System.out.println("Author Raw: " + raw);
        Set<BookAuthorRawDto> books = authorRepository.findAuthorByBookId(id);

        return new AuthorDetailDto(
                raw.id(),
                raw.name(),
                raw.bio(),
                books
        );
    }
}
