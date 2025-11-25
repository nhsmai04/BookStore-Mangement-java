package org.librarymanagement.service;

import org.librarymanagement.repository.BookRepository;

public interface SlugService {
    public String generateUniqueSlug(String title);
}
