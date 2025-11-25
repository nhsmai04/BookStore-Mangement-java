package org.librarymanagement.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.ss.usermodel.*;
import org.hibernate.collection.spi.PersistentList;
import org.librarymanagement.constant.*;
import org.librarymanagement.service.*;

import org.librarymanagement.entity.*;
import org.librarymanagement.dto.response.*;
import org.librarymanagement.repository.*;
import org.librarymanagement.dto.response.BookDetailResponse;
import org.librarymanagement.dto.response.ReviewResponse;
import org.librarymanagement.dto.response.UserResponse;
import org.opencv.core.Mat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.librarymanagement.dto.response.BookListDto;
import org.librarymanagement.dto.response.BookRawDto;
import org.librarymanagement.entity.Book;
import org.librarymanagement.entity.BookAuthor;
import org.librarymanagement.entity.Publisher;
import org.librarymanagement.entity.Review;
import org.librarymanagement.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Service
@Transactional
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final AuthorRepository authorRepository;
    private final  PublisherRepository publisherRepository;
    private final GenreRepository genreRepository;
    private final AuthorService authorService;
    private final SlugService slugService;
    private final BookVersionService bookVersionService;
    private final GenreService genreService;
    private final PublisherService publisherService;
    private final ImageProcessingService  imageProcessingService;
    private final OCRService ocrService;
    private final BorrowService borrowService;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, UserRepository userRepository, AuthorService authorService,
                           SlugService slugService, BookVersionService bookVersionService,
                           GenreService genreService, PublisherService publisherService, ImageProcessingService imageProcessingService
                            , OCRService ocrService, BorrowService borrowService,
                           AuthorRepository authorRepository, PublisherRepository publisherRepository, GenreRepository genreRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
        this.genreRepository = genreRepository;
        this.authorService = authorService;
        this.slugService = slugService;
        this.bookVersionService = bookVersionService;
        this.genreService = genreService;
        this.publisherService = publisherService;
        this.imageProcessingService = imageProcessingService;
        this.ocrService = ocrService;
        this.borrowService = borrowService;
    }

    public Page<BookListDto> findAllBooksWithFilter(String author, String publisher, String genre, Pageable pageable) {
        Page<BookRawDto> rawBooks = bookRepository.findAllBooksRaw(author, publisher, genre, pageable);

        // Step 1: Group theo sách (title + publisher)
        // Gom authors
        Map<Integer, Set<String>> authorsMap = rawBooks.getContent().stream()
                .filter(dto -> dto.bookAuthor() != null)
                .collect(Collectors.groupingBy(
                        BookRawDto::id,
                        LinkedHashMap::new,
                        Collectors.mapping(BookRawDto::bookAuthor, Collectors.toSet())
                ));

        // Gom genres
        Map<Integer, Set<String>> genresMap = rawBooks.getContent().stream()
                .filter(dto -> dto.bookGenre() != null)
                .collect(Collectors.groupingBy(
                        BookRawDto::id,
                        LinkedHashMap::new,
                        Collectors.mapping(BookRawDto::bookGenre, Collectors.toSet())
                ));

        // Step 2: Map sang BookDto
        List<BookListDto> dtos = rawBooks.getContent().stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                BookRawDto::id,
                                dto -> new BookListDto(
                                        dto.id(),
                                        dto.bookImage(),
                                        dto.bookTitle(),
                                        dto.bookDescription(),
                                        authorsMap.getOrDefault(dto.id(), Set.of()), // gom nhiều tác giả
                                        dto.bookPublisher(),
                                        genresMap.getOrDefault(dto.id(), Set.of()),
                                        dto.totalCurrent()
                                ),
                                (existing, newDto) -> existing,
                                LinkedHashMap::new
                        ),
                        m -> new ArrayList<>(m.values())
                ));

        // Step 3: Trả về Page<BookDto>
        return new PageImpl<>(dtos, pageable, rawBooks.getTotalElements());
    }

    @Override
    public Book findBookBySlug(String slug) {
        return bookRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sách với slug: " + slug));
    }
    @Override
    public BookDetailResponse createBookDetailResponseBySlug(String slug) {
        Book book = findBookBySlug(slug);
        BookDetailResponse bookDetailResponse = convertToBookDetaiResponse(book);
        return bookDetailResponse;
    }

    private Set<ReviewResponse> convertReviewsToDtos(Set<Review> reviews) {
        if (reviews == null) {
            return Collections.emptySet();
        }

        Set<ReviewResponse> reviewResponses = new HashSet<>();

        reviewResponses = reviews.stream()
                .map(review -> new ReviewResponse(
                        review.getId(),
                        review.getComment(),
                        review.getStar(),
                        review.getCreatedAt(),
                        new UserResponse(
                                review.getUser().getId(),
                                review.getUser().getName(),
                                review.getUser().getUsername()
                        )
                ))
                .collect(Collectors.toSet());

        return reviewResponses;
    }

    @Override
    public void importBooksFromExcel(MultipartFile file) throws IOException {

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // Bỏ dòng header
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                log.info("---- Import row {} ----", i + 1);

                Row row = sheet.getRow(i);
                if (row == null) continue;

                String title = getCellValueAsString(row.getCell(0));
                log.info("Title: [{}]", title);
                if (title == null || title.isEmpty()) {
                    log.warn("Row {} skipped: empty title", i + 1);
                    continue; // bỏ dòng không có book name
                }

                Book book = new Book();
                book.setTitle(getCellValueAsString(row.getCell(0)));
                book.setDescription(getCellValueAsString(row.getCell(4)));

                // --- Publisher ---
                String publisherName = getCellValueAsString(row.getCell(5));
                log.info("Publisher name: [{}]", publisherName);
                Publisher publisher = publisherService.findOrCreatePublisher(publisherName);
                log.info("Publisher id: {}", publisher.getId());
                book.setPublisher(publisher);

                // --- Published Day ---
                String publishedDayStr = getCellValueAsString(row.getCell(6));
                log.info("Published day raw: [{}]", publishedDayStr);
                try{
                    if (publishedDayStr != null && !publishedDayStr.isEmpty()) {
                        book.setPublishedDay(LocalDate.parse(publishedDayStr));
                    } else {
                        book.setPublishedDay(LocalDate.now());
                    }
                }catch (Exception e){
                    log.error("Invalid published day at row {}: {}", i + 1, publishedDayStr);
                    throw e;
                }


                // --- Quantity ---
                Integer totalQuantity = parseInteger(getCellValueAsString(row.getCell(7)));
                log.info("Quantity raw: [{}] -> parsed: {}", totalQuantity, totalQuantity);
                if (totalQuantity <= 0) {
                    log.warn("Row {} has invalid quantity, defaulting to 1", i + 1);
                    totalQuantity = 1;
                }
                book.setTotalQuantity(totalQuantity);
                book.setTotalCurrent(totalQuantity);

                // --- Image ---
                book.setImage(getCellValueAsString(row.getCell(8)));

                // --- Slug ---
                String slug = slugService.generateUniqueSlug(book.getTitle());
                log.info("Generated book slug: {}", slug);
                book.setSlug(slug);

                // --- Author (status=1) ---
                String authorName = getCellValueAsString(row.getCell(1));
                log.info("Author: [{}]", authorName);
                if (authorName != null && !authorName.isEmpty()) {
                    Author author = authorService.findOrCreateAuthor(authorName);
                    log.info("Author id: {}", author.getId());
                    BookAuthor bookAuthor = new BookAuthor();
                    bookAuthor.setBook(book);
                    bookAuthor.setAuthor(author);
                    bookAuthor.setStatus(AuthorConstants.AUTHOR);
                    book.getBookAuthors().add(bookAuthor);
                }

                // --- Coauthor (status=2, có thể nhiều, cách nhau dấu ,) ---
                String coAuthors = getCellValueAsString(row.getCell(2));
                if (coAuthors != null && !coAuthors.isEmpty()) {
                    String[] coAuthorArr = coAuthors.split(",");
                    for (String co : coAuthorArr) {
                        String coName = co.trim();
                        if (coName.isEmpty()) continue;
                        Author coAuthor = authorService.findOrCreateAuthor(coName);
                        BookAuthor bookAuthor = new BookAuthor();
                        bookAuthor.setBook(book);
                        bookAuthor.setAuthor(coAuthor);
                        bookAuthor.setStatus(AuthorConstants.COAUTHOR);
                        book.getBookAuthors().add(bookAuthor);
                    }
                }

                // --- Genres (có thể nhiều, cách nhau dấu ,) ---
                String genres = getCellValueAsString(row.getCell(3));
                if (genres != null && !genres.isEmpty()) {
                    String[] genreArr = genres.split(",");
                    for (String g : genreArr) {
                        String gName = g.trim();
                        if (gName.isEmpty()) continue;
                        Genre genre = genreService.findOrCreateGenre(gName);
                        BookGenre bookGenre = new BookGenre();
                        bookGenre.setBook(book);
                        bookGenre.setGenre(genre);
                        book.getBookGenres().add(bookGenre);
                    }
                }
                log.info(
                        "Saving book: title={}, totalQuantity={}, totalCurrent={}",
                        book.getTitle(),
                        book.getTotalQuantity(),
                        book.getTotalCurrent()
                );
                bookRepository.save(book);
                log.info("Saved book id={}", book.getId());
                // Tạo book_versions
                log.info(
                        "Creating {} book versions for book id={}",
                        book.getTotalQuantity(),
                        book.getId()
                );
                bookVersionService.createBookVersions(book, book.getTotalQuantity(), BookVersionConstants.AVAILABLE);

            }
        }
    }
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? cell.getLocalDateTimeCellValue().toLocalDate().toString()
                    : String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            case BLANK, _NONE, ERROR -> null;
        };
    }

    private Integer parseInteger(String value) {
        try {
            return value == null ? 0 : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /*Tìm kiếm sách - 91162*/
    @Override
    @Transactional(readOnly = true)
    public Page<BookResponseDto> searchBooks(String keyword, Pageable pageable) {
        Page<BookSearchFlatDto> flat = bookRepository.searchBooks(keyword, pageable);

        if(flat.isEmpty()){
            throw new NotFoundException("Không tìm thấy sách với từ khóa " + keyword);
        }

        List<Integer> bookIds = flat.getContent().stream()
                .map(BookSearchFlatDto::id)
                .collect(Collectors.toList());

        List<BookSearchFlatDto> allBookData = bookRepository.findAllBookDataByIds(bookIds);

        Map<Integer, Set<String>> authorsMap = allBookData.stream()
                .filter(dto -> dto.bookAuthor() != null)
                .collect(Collectors.groupingBy(
                        BookSearchFlatDto::id,
                        LinkedHashMap::new,
                        Collectors.mapping(BookSearchFlatDto::bookAuthor, Collectors.toSet())
                ));

        Map<Integer, Set<String>> genresMap = allBookData.stream()
                .filter(dto -> dto.bookGenre() != null)
                .collect(Collectors.groupingBy(
                        BookSearchFlatDto::id,
                        LinkedHashMap::new,
                        Collectors.mapping(BookSearchFlatDto::bookGenre, Collectors.toSet())
                ));

        List<BookResponseDto> results = flat.getContent().stream()
                .map(dto -> new BookResponseDto(
                        dto.id(),
                        dto.image(),
                        dto.title(),
                        dto.description(),
                        dto.publishedDay(),
                        dto.publisherName(),
                        authorsMap.getOrDefault(dto.id(), Set.of()),
                        genresMap.getOrDefault(dto.id(), Set.of())
                ))
                .distinct()
                .collect(Collectors.toList());

        return new PageImpl<>(results, pageable, flat.getTotalElements());
    }

    @Override
    public void uploadBooksFromImage(MultipartFile pdfFile) throws IOException {
        try(PDDocument document = PDDocument.load(pdfFile.getInputStream()))
        {
            //Chuyen tu PDF sang hình ảnh
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            StringBuilder fullText = new StringBuilder();

            // Tiền xử lý ảnh
            for(int page = 0; page < document.getNumberOfPages(); page++) {
                // Render trang PDF thành BufferedImage (DPI 300 là tối ưu cho OCR)
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300);

                // Tiền xử lý ảnh
                Mat preprocessedImage = null;
                try{
                    System.out.println("Tien xu ly anh bat dau: ->");
                    preprocessedImage = imageProcessingService.preprocessImage(bim);

                    // 3. Thực hiện OCR
                    System.out.println("OCR bat dau: ->");
                    String pageText = ocrService.performOCR(preprocessedImage);
                    fullText.append(pageText).append("\n");
                }finally {
                    // Đảm bảo giải phóng bộ nhớ Native kể cả khi OCR lỗi
                    if (preprocessedImage != null) {
                        preprocessedImage.release();
                    }
                }
            }
            System.out.println("Toàn bộ nội dung sách: " + fullText.toString());
            // Các xử lý tiếp theo như lưu trữ sách vào cơ sở dữ liệu, v.v.
        } catch (Exception e) {
            // Xử lý ngoại lệ: log lỗi hoặc gửi thông báo lỗi
            e.printStackTrace();
            // Thông báo lỗi cho người dùng nếu cần
            System.out.println("Error during image preprocessing: " + e.getMessage());
        }
    }

    private BookDetailResponse convertToBookDetaiResponse(Book book)
    {
        Set<BookAuthor> bookAuthors = book.getBookAuthors();

        List<String> authorNames = new ArrayList<>();

        authorNames = bookAuthors.stream()
                .map(bookAuthor -> bookAuthor.getAuthor().getName())
                .collect(Collectors.toList());

        Publisher publisher = book.getPublisher();

        String publisherName = (publisher != null) ? publisher.getName() : null;

        Set<Review> reviews = book.getReviews();

        Set<ReviewResponse> reviewResponses = convertReviewsToDtos(reviews);

        BookDetailResponse bookDetailResponse = new BookDetailResponse(
                book.getId(),
                book.getImage(),
                book.getTitle(),
                book.getTotalCurrent(),
                book.getTotalQuantity(),
                book.getDescription(),
                book.getPublishedDay(),
                authorNames,
                publisherName,
                reviewResponses
        );

        return bookDetailResponse;
    }

    @Override
    public BookDetailResponse getBookDetailById(Integer id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sách với id: " + id));

        System.out.println("Gia tri publishedDay sau khi lay ra la: " + book.getPublishedDay());
        BookDetailResponse bookDetailResponse = convertToBookDetaiResponse(book);
        return bookDetailResponse;
    }

    @Override
    public ResponseObject updateBookDetails(Integer id, BookDetailResponse bookDetails) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isEmpty()) {
            return new ResponseObject("Không tìm thấy sách với id: " + id, 404, null);
        }
        System.out.println("Gia tri publishedDay sau khi lay ra la: " + optionalBook.get().getPublishedDay());
        Book book = optionalBook.get();
        book.setTitle(bookDetails.title());
        book.setDescription(bookDetails.description());
        book.setPublishedDay(bookDetails.publishedDay());
        book.setTotalQuantity(bookDetails.totalQuantity());
        book.setTotalCurrent(bookDetails.totalCurrent());
        book.setImage(bookDetails.image());

        // Xoá tất cả các tác giả cũ trước khi thêm tác giả mới
        book.getBookAuthors().clear();

        // Cập nhật tác giả
        List<String> authorNames = bookDetails.authorName();
        if (authorNames != null) {
            int i = 0;
            for (String authorName : authorNames) {
                System.out.println("Processing author: " + authorName);
                Author author = authorService.findOrCreateAuthor(authorName);
                BookAuthor bookAuthor = new BookAuthor();
                bookAuthor.setBook(book);
                bookAuthor.setAuthor(author);
                bookAuthor.setStatus(i == 0 ? AuthorConstants.AUTHOR : AuthorConstants.COAUTHOR);
                book.getBookAuthors().add(bookAuthor);
                i++;
            }
        }

        String publisherName = bookDetails.publisherName();
        if (publisherName == null) {
            publisherName = "";
        }
        Publisher publisher = publisherService.findOrCreatePublisher(publisherName);
        book.setPublisher(publisher);

        // Lưu lại sách với các thay đổi
        bookRepository.save(book);

        return new ResponseObject("Cập nhật thông tin sách thành công", 200, null);
    }

}
