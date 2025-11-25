package org.librarymanagement.controller.admin;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.bcel.Const;
import org.librarymanagement.constant.ApiEndpoints;
import org.librarymanagement.constant.BookVersionConstants;
import org.librarymanagement.dto.request.BookFilterRequest;
import org.librarymanagement.dto.response.BookDetailResponse;
import org.librarymanagement.dto.response.BookListDto;
import org.librarymanagement.dto.response.BorrowResponse;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.entity.Author;
import org.librarymanagement.entity.BookVersion;
import org.librarymanagement.service.*;
import org.librarymanagement.utils.ExcelValidator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@Controller("adminBookController")
@RequestMapping(ApiEndpoints.ADMIN_BOOK)
public class BookController {

    private final BookService bookService;
    private final BookVersionService bookVersionService;
    private final AuthorService authorService;
    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;


    public BookController(BookService bookService, BookVersionService bookVersionService,
                          AuthorService authorService,
                          NotificationService notificationService, CurrentUserService currentUserService) {
        this.bookService = bookService;
        this.bookVersionService = bookVersionService;
        this.authorService = authorService;
        this.notificationService = notificationService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public String showBooklist(
            BookFilterRequest filter,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        model.addAttribute("author", filter.author());
        model.addAttribute("publisher", filter.publisher());
        model.addAttribute("genre", filter.genre());

        Pageable pageable = PageRequest.of(page, size);
        Page<BookListDto> bookPage = bookService.findAllBooksWithFilter(
                filter.author(),
                filter.publisher(),
                filter.genre(),
                pageable
        );

        int totalPages = bookPage.getTotalPages();

        // Danh sách rỗng
        if (totalPages == 0) {
            model.addAttribute("books", List.of());
            model.addAttribute("totalPages", 0);
            model.addAttribute("currentPage", 1);
            model.addAttribute("size", size);
        }
        // Page vượt quá tổng số trang, chuyển về trang có số trang lớn nhất
        else if (page >= totalPages) {
            UriComponentsBuilder b = UriComponentsBuilder.fromPath("/admin/books")
                    .queryParam("page", totalPages - 1)
                    .queryParam("size", size);

            if (filter.author() != null && !filter.author().isBlank())
                b.queryParam("author", filter.author());
            if (filter.publisher() != null && !filter.publisher().isBlank())
                b.queryParam("publisher", filter.publisher());
            if (filter.genre() != null && !filter.genre().isBlank())
                b.queryParam("genre", filter.genre());

            String redirectUrl = b.encode().build().toUriString(); // auto URL-encode
            return "redirect:" + redirectUrl;
        } else {
            model.addAttribute("books", bookPage.getContent());
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("currentPage", page);
            model.addAttribute("size", size);
        }
        return "admin/books/index";
    }

    @GetMapping("/{id}/edit")
    public String editBook(
            @PathVariable("id") Integer id,
            Model model

    ) {
        BookDetailResponse bookDetail = bookService.getBookDetailById(id);
        if (bookDetail == null) {
            // Optionally, redirect or show an error page/message
            return "redirect:/admin/books?error=notfound";
        }
        List<Author> authors = authorService.getAllAuthorsRaw();
        if(authors == null) {
            authors = List.of();
        }

        model.addAttribute("book", bookDetail);
        model.addAttribute("authors", authors);
        return "admin/books/edit";
    }


    @GetMapping("/{id}")
    public String showBookdetail(
            @PathVariable("id") Integer id,
            @PageableDefault(size = 10) Pageable pageable,
            Model model
    ) {
        BookDetailResponse bookDetail = bookService.getBookDetailById(id);
        if (bookDetail == null) {
            // Optionally, redirect or show an error page/message
            return "redirect:/admin/books?error=notfound";
        }
        Page<BookVersion> bookVersions = bookVersionService.findBookVersionsByBookId(id, pageable);
        model.addAttribute("book", bookDetail);
        model.addAttribute("bookVersions", bookVersions);
        return "admin/books/detail";
    }

    @PostMapping("/import")
    public String importBooks(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            // Validate trước khi import
            List<String> errors = ExcelValidator.validateExcelFile(file.getInputStream());
            if (!errors.isEmpty()) {
                redirectAttributes.addFlashAttribute("message", "Import thất bại, file không hợp lệ!");
                redirectAttributes.addFlashAttribute("errors", errors); // gửi danh sách lỗi sang view
                redirectAttributes.addFlashAttribute("alertType", "danger");
                return "redirect:" + ApiEndpoints.ADMIN_BOOK;
            }

            // Nếu file hợp lệ thì mới gọi service để import
            bookService.importBooksFromExcel(file);
            notificationService.notify(
                    "Import sách từ file Excel",
                    "Sách mới đã được thêm vào hệ thống thông qua việc import từ file Excel.",
                    "IMPORT_BOOKS",
                    currentUserService.getCurrentUser()
            );
            redirectAttributes.addFlashAttribute("message", "Import thành công!");
            redirectAttributes.addFlashAttribute("alertType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Import thất bại: " + e.toString());
            redirectAttributes.addFlashAttribute("alertType", "danger");
        }
        return "redirect:" + ApiEndpoints.ADMIN_BOOK;
    }

    @GetMapping("/download-template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        ClassPathResource resource = new ClassPathResource("static/public/files/Template.xlsx");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=book_template.xlsx");
        StreamUtils.copy(resource.getInputStream(), response.getOutputStream());
    }

    @PostMapping("/{id}/edit")
    public String updateBook(
            @PathVariable("id") Integer id,
            @ModelAttribute BookDetailResponse bookDetailResponse,
            RedirectAttributes redirectAttributes
    ) {
        ResponseObject response = bookService.updateBookDetails(id, bookDetailResponse);
        if (response.status().intValue() == 200 ) {
            notificationService.notify(
                    "Cập nhật thông tin sách",
                    "Thông tin sách '" + bookDetailResponse.title() + "' đã được cập nhật.",
                    "UPDATE_BOOK",
                    currentUserService.getCurrentUser()

            );
            redirectAttributes.addFlashAttribute("message", "Cập nhật sách thành công!");
            redirectAttributes.addFlashAttribute("alertType", "success");
        } else {
            redirectAttributes.addFlashAttribute("message", "Cập nhật sách thất bại: " + response.message());
            redirectAttributes.addFlashAttribute("alertType", "danger");
        }
        return "redirect:" + ApiEndpoints.ADMIN_BOOK + "/" + id + "/edit";
    }
    
    @GetMapping("/book-version/edit")
    public String editBookVersion(
            @RequestParam("bookId") Integer bookId,
            @PageableDefault(size = 10) Pageable pageable,
            Model model
    ) {
        Page<BookVersion> bookVersions = bookVersionService.findBookVersionsByBookId(bookId, pageable);
        model.addAttribute("bookVersions", bookVersions);
        return "admin/books/book_versions/edit";
    }

    @PostMapping("/book-version/edit")
    @ResponseBody
    public ResponseObject updateBookVersion(
            @RequestParam("bookId") Integer bookId,
            @RequestBody Map<String, Integer> data
    ) {
        Integer status = data.get("status");
        ResponseObject responseObject = bookVersionService.updateBookVersionDetails(bookId, status);
        if(responseObject.status().intValue() == 200) {
            notificationService.notify(
                    "Cập nhật trạng thái phiên bản sách",
                    "Trạng thái phiên bản sách với ID '" + bookId + "' đã được cập nhật.",
                    "UPDATE_BOOK_VERSION",
                    currentUserService.getCurrentUser()
            );
        }
        return bookVersionService.updateBookVersionDetails(bookId, status);
    }


}
