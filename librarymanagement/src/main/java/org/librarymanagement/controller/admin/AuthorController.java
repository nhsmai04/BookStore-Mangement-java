package org.librarymanagement.controller.admin;

import org.librarymanagement.constant.ApiEndpoints;
import org.librarymanagement.dto.response.AuthorDetailDto;
import org.librarymanagement.dto.response.AuthorListDto;
import org.librarymanagement.service.AuthorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Controller
@RequestMapping(ApiEndpoints.ADMIN_AUTHOR)
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public String showAuthorlist(
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuthorListDto> authorPage = authorService.getAllAuthors(pageable);
        int totalPages = authorPage.getTotalPages();

        if (authorPage.isEmpty()) {
            model.addAttribute("message", "No authors found.");
        } else {
            model.addAttribute("authorPage", authorPage);
        }
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "admin/authors/index";
    }

    @GetMapping("/{id}/edit")
    public String editBook() {
        return "admin/authors/edit";
    }

    @GetMapping("/{id}")
    public String showAuthordetail(
            @PathVariable("id") Integer id,
            Model model
    ) {
        AuthorDetailDto authorDetailDto = authorService.getAuthorDetailById(id);
        model.addAttribute("author", authorDetailDto);
        return "admin/authors/detail";
    }
}
