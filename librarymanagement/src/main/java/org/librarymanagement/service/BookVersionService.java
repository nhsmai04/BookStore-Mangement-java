package org.librarymanagement.service;

import org.librarymanagement.entity.Book;
import org.librarymanagement.entity.BookVersion;

import java.util.List;

public interface BookVersionService {
    List<BookVersion> createBookVersions(Book book, int quantity, int status);
}
