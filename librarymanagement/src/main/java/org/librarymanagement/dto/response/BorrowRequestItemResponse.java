package org.librarymanagement.dto.response;

import java.util.List;

public record BorrowRequestItemResponse(
        Integer id,
        String status,
        BookVersionResponse book
) {}
