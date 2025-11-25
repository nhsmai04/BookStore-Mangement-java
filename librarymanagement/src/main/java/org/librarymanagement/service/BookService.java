package org.librarymanagement.service;

import org.librarymanagement.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.librarymanagement.entity.Book;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BookService {
    Page<BookListDto> findAllBooksWithFilter(String author, String publisher, String genre, Pageable pageable);
    BookDetailResponse createBookDetailResponseBySlug(String slug);
    Book findBookBySlug(String slug);
    void importBooksFromExcel(MultipartFile file) throws IOException;
    Page<BookResponseDto> searchBooks(String keyword, Pageable pageable);
    void uploadBooksFromImage(MultipartFile file) throws IOException;
    BookDetailResponse getBookDetailById(Integer id);
    ResponseObject updateBookDetails(Integer id, BookDetailResponse bookDetails);
}
