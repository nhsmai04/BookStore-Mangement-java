package org.librarymanagement.dto.response;

public record BorrowRequestStatDto(
        Integer currentWeekCount,
        Integer lastWeekCount,
        Double percentChange
) {
}
