package org.librarymanagement.dto.response;

public record BookStatDto (
    Integer currentWeekBooks,
    Integer lastWeekBooks,
    Double percentChange
) {
}
