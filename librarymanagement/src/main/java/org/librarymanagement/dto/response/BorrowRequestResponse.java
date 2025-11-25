package org.librarymanagement.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record BorrowRequestResponse(
        Integer id,
        Integer quantity,
        String status,
        LocalDateTime dayConfirmed,
        List<BorrowRequestItemResponse> items
){}
