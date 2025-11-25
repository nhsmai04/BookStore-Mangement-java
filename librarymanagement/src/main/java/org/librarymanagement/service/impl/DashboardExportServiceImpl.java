package org.librarymanagement.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import org.librarymanagement.dto.response.*;
import org.librarymanagement.service.DashBoardService;
import org.librarymanagement.service.DashboardExportService;
import org.librarymanagement.service.PdfExportService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardExportServiceImpl implements DashboardExportService {
    private final DashBoardService dashBoardService;
    private final PdfExportService pdfExportService;

    public DashboardExportServiceImpl(DashBoardService dashBoardService, PdfExportService pdfExportService) {
        this.dashBoardService = dashBoardService;
        this.pdfExportService = pdfExportService;
    }

    public void exportPdf(HttpServletResponse response) {
        DashboardExportDto data = collectData();
        pdfExportService.exportDashBoard(response, data);
    }

    private DashboardExportDto collectData() {

        BorrowRequestStatDto borrowRequestStat =
                dashBoardService.getBorrowRequestStat();

        BookStatDto bookStat =
                dashBoardService.getBookStat();

        BookRequestStatDto bookRequestStat =
                dashBoardService.getBookRequestStat();

        UserStatDto userStat =
                dashBoardService.getUserStat();

        List<BorrowRequestDetailDto> recentBorrows =
                dashBoardService.getRecentBorrowRequests(5);

        int year = LocalDate.now().getYear();

        List<Long> borrowByMonths =
                dashBoardService.getMonthlyBorrowCounts(year);

        List<Long> newUsersByMonths =
                dashBoardService.getMonthlyNewUserCounts(year);

        return new DashboardExportDto(
                borrowRequestStat,
                bookStat,
                bookRequestStat,
                userStat,
                recentBorrows,
                borrowByMonths,
                newUsersByMonths,
                year
        );
    }
}
