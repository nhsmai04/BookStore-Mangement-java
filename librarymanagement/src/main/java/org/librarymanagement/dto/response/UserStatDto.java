package org.librarymanagement.dto.response;

public record UserStatDto(
        Integer currentWeekUsers,
        Integer lastWeekUsers,
        Double percentChange
) {
}
