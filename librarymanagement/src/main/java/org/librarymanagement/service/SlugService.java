package org.librarymanagement.service;

import org.librarymanagement.repository.BookRepository;

import java.util.Set;

public interface SlugService {
    public String generateUniqueSlug(String title);
    public String generateUniqueSlugInMemory(String title, Set<String> usedSlugs);
}
