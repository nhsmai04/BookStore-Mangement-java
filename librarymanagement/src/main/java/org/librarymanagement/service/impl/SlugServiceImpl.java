package org.librarymanagement.service.impl;

import org.librarymanagement.repository.BookRepository;
import org.librarymanagement.service.SlugService;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Locale;

@Service
public class SlugServiceImpl implements SlugService {
    private final BookRepository bookRepository;

    public SlugServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public String generateUniqueSlug(String title) {
        // Tạo slug cơ bản
        String baseSlug = toSlug(title);

        // Giới hạn độ dài slug (75 ký tự)
        if (baseSlug.length() > 75) {
            baseSlug = baseSlug.substring(0, 75).replaceAll("-+$", ""); // cắt + bỏ dấu "-" ở cuối
        }

        String slug = baseSlug;

        // Kiểm tra DB, nếu trùng thì thêm hậu tố -1, -2...
        int counter = 1;
        while (bookRepository.existsBySlug(slug)) {
            String suffix = "-" + counter++;
            int maxLength = 75 - suffix.length();

            String truncatedBase = baseSlug.length() > maxLength
                    ? baseSlug.substring(0, maxLength).replaceAll("-+$", "")
                    : baseSlug;

            slug = truncatedBase + suffix;
        }

        return slug;
    }

    private String toSlug(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String slug = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", ""); // bỏ dấu
        slug = slug.toLowerCase(Locale.ENGLISH).replaceAll("[^a-z0-9\\s-]", "");      // bỏ ký tự đặc biệt
        slug = slug.replaceAll("\\s+", "-");                                         // khoảng trắng -> "-"
        return slug.replaceAll("-{2,}", "-").replaceAll("^-|-$", "");                 // bỏ dấu "-" thừa
    }
}
