package org.librarymanagement.service;

import org.librarymanagement.constant.BookVersionConstants;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.entity.Book;
import org.librarymanagement.entity.BookVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookVersionService {
    List<BookVersion> createBookVersions(Book book, int quantity, int status);
    Page<BookVersion> findBookVersionsByBookId(Integer bookId, Pageable pageable);
    ResponseObject updateBookVersionDetails(Integer id, Integer status);
}
