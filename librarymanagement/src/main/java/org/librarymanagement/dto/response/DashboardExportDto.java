package org.librarymanagement.dto.response;

import java.util.List;

public record DashboardExportDto(
        BorrowRequestStatDto borrowRequestStat,
        BookStatDto bookStat,
        BookRequestStatDto bookRequestStat,
        UserStatDto userStat,
        List<BorrowRequestDetailDto> recentBorrows,
        List<Long> borrowCountByMonths,
        List<Long> newUserCountByMonths,
        int year
) {
}
