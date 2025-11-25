package org.librarymanagement.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record BookDetailResponse(
        Integer id,
        String image,
        String title,
        Integer totalCurrent,
        Integer totalQuantity,
        String description,
        LocalDate publishedDay,
        List<String> authorName,
        String publisherName,
        Set<ReviewResponse> reviews
) {}
