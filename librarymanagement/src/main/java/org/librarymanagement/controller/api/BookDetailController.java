package org.librarymanagement.controller.api;

import org.librarymanagement.constant.ApiEndpoints;
import org.librarymanagement.dto.response.BookDetailResponse;
import org.librarymanagement.dto.response.ReviewResponse;
import org.librarymanagement.dto.response.UserResponse;
import org.librarymanagement.entity.Book;
import org.librarymanagement.entity.BookAuthor;
import org.librarymanagement.entity.Publisher;
import org.librarymanagement.entity.Review;
import org.librarymanagement.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping(ApiEndpoints.USER_BOOK)
public class BookDetailController {

    private final BookService bookService;

    @Autowired
    public BookDetailController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/{slug}")
    public ResponseEntity<BookDetailResponse> getBook(@PathVariable String slug) {

        BookDetailResponse bookDetailResponse = bookService.createBookDetailResponseBySlug(slug);

        return ResponseEntity.ok(bookDetailResponse);
    }
}

