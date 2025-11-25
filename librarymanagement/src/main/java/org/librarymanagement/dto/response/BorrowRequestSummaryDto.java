package org.librarymanagement.dto.response;

import org.librarymanagement.constant.BRStatusConstant;

import java.time.LocalDateTime;

public record BorrowRequestSummaryDto(
        Integer id,
        String username,
        Integer totalBooks,
        LocalDateTime borrowDate,
        BRStatusConstant status
) {}
