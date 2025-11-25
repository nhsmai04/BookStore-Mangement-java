package org.librarymanagement.service.impl;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.entity.Book;
import org.librarymanagement.entity.User;
import org.librarymanagement.exception.NotFoundException;
import org.librarymanagement.repository.BookRepository;
import org.librarymanagement.repository.UserRepository;
import org.librarymanagement.service.BorrowService;
import org.librarymanagement.service.PdfService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
public class PdfServiceImpl implements PdfService {
    private final UserRepository userRepository;
    private final BorrowService borrowService;
    private final BookRepository bookRepository;

    public PdfServiceImpl(UserRepository userRepository, BorrowService borrowService, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.borrowService = borrowService;
        this.bookRepository = bookRepository;
    }

    //Upload borrowbook from PDFfile
    @Override
    public ResponseObject uploadBooksFromPdf(MultipartFile pdfFile)  {
        try (PDDocument document = PDDocument.load(pdfFile.getInputStream())) {
            org.apache.pdfbox.text.PDFTextStripper pdfStripper = new org.apache.pdfbox.text.PDFTextStripper();
            String text = pdfStripper.getText(document);
            // Finding User by username
            User user = convertToUser(text);
            Map<Integer, Integer> borrowBooks = convertToBorrowResponse(text);
            for (Map.Entry<Integer, Integer> entry : borrowBooks.entrySet()) {
                System.out.println("Book ID: " + entry.getKey() + ", Quantity: " + entry.getValue());
            }
            return borrowService.borrowBook(borrowBooks, user);
        } catch (Exception e) {
            return new ResponseObject("Error extracting text from PDF: " + e.getMessage(), 500, null);
        }
    }

    private User convertToUser(String text) {
        String email = "unknown@example.com";
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("Email:")) {
                email = line.substring("Email:".length()).trim();
                break;
            }
        }
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found with email: " + email);
        }
        return user;
    }

    private Map<Integer, Integer> convertToBorrowResponse(String text) {
        Map<Integer, Integer> result = new HashMap<>();
        String[] lines = text.split("\\r?\\n");
        boolean isDataSection = false;
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("STT")) {
                isDataSection = true;
                continue;
            }
            if (isDataSection) {
                if (line.isEmpty() || line.startsWith("Ghi chú")) break;
                // Match: number, book name, number (e.g., 1 Mắt biếc 1)
                // Regex: ^(\d+)\s+(.+)\s+(\d+)$
                java.util.regex.Matcher m = java.util.regex.Pattern
                        .compile("^(\\d+)\\s+(.+)\\s+(\\d+)$")
                        .matcher(line);
                if (m.matches()) {
                    String bookName = m.group(2).trim();
                    int quantity = Integer.parseInt(m.group(3));
                    Book book = bookRepository.findBooksByTitle(bookName);
                    if (book != null) {
                        result.put(book.getId(), quantity);
                    } else {
                        System.out.println("Book not found: " + bookName);
                    }
                }
            }
        }
        return result;
    }
}
