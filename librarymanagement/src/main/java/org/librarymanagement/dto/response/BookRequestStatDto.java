package org.librarymanagement.dto.response;

public record BookRequestStatDto(
        Integer currentWeekBookRequests,
        Integer lastWeekBookRequests,
        Double percentChange
)
{
}
