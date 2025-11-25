package org.librarymanagement.service.impl;


import org.librarymanagement.constant.BookVersionConstants;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.entity.Book;
import org.librarymanagement.entity.BookVersion;
import org.librarymanagement.repository.BookVersionRepository;
import org.librarymanagement.service.BookVersionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookVersionServiceImpl implements BookVersionService {
    private final BookVersionRepository bookVersionRepository;

    public BookVersionServiceImpl(BookVersionRepository bookVersionRepository) {
        this.bookVersionRepository = bookVersionRepository;
    }

    @Override
    public List<BookVersion> createBookVersions(Book book, int quantity, int status) {
        List<BookVersion> versions = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            BookVersion version = new BookVersion();
            version.setBook(book);
            version.setStatus(status);
            versions.add(version);
        }
        return bookVersionRepository.saveAll(versions);
    }
    @Override
    public Page<BookVersion> findBookVersionsByBookId(Integer bookId, Pageable pageable) {
        return bookVersionRepository.findBookVersionsByBookId(bookId, pageable);
    }

    @Override
    public ResponseObject updateBookVersionDetails(Integer id, Integer status)
    {
        System.out.println("gia tri id truyen vao la :"+ id);
        BookVersion existingVersion = bookVersionRepository.findById(id).orElse(null);

        if (existingVersion == null) {
            return new ResponseObject("Error", 404 , null);
        }

        existingVersion.setStatus(status);
        bookVersionRepository.save(existingVersion);
        return new ResponseObject("Success", 200 , null);
    }
}
