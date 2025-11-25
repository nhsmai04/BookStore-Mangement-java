package org.librarymanagement.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.librarymanagement.dto.response.*;
import org.librarymanagement.service.PdfExportService;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfExportServiceImpl implements PdfExportService {

    // ================= EXPORT =================
    @Override
    public void exportDashBoard(HttpServletResponse response,
                                DashboardExportDto data) {

        try {
            response.setContentType("application/pdf");
            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=dashboard-report.pdf"
            );

            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            addTitle(document, data.year());
            addOverviewSection(document, data);
            addRecentBorrowTable(document, data.recentBorrows());
            addMonthlyStatisticTable(document, data);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF", e);
        }
    }

    // ================= TITLE =================
    private void addTitle(Document document, int year)
            throws DocumentException {

        Paragraph title = new Paragraph(
                "HỆ THỐNG QUẢN LÝ THƯ VIỆN",
                roboto(18, true)
        );
        title.setAlignment(Element.ALIGN_CENTER);

        Paragraph subtitle = new Paragraph(
                "Báo cáo thống kê Dashboard - Năm " + year,
                roboto(12, false)
        );
        subtitle.setAlignment(Element.ALIGN_CENTER);

        document.add(title);
        document.add(subtitle);
        document.add(Chunk.NEWLINE);
    }

    // ================= OVERVIEW =================
    private void addOverviewSection(Document document,
                                    DashboardExportDto data)
            throws DocumentException {

        document.add(new Paragraph("TỔNG QUAN", roboto(14, true)));
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(15);

        table.addCell(cell("Yêu cầu mượn (tuần này)", true));
        table.addCell(cell(String.valueOf(
                data.borrowRequestStat().currentWeekCount()), false));

        table.addCell(cell("Sách được mượn (tuần này)", true));
        table.addCell(cell(String.valueOf(
                data.bookRequestStat().currentWeekBookRequests()), false));

        table.addCell(cell("Sách mới (tháng này)", true));
        table.addCell(cell(String.valueOf(
                data.bookStat().currentWeekBooks()), false));

        table.addCell(cell("Người dùng mới (tuần này)", true));
        table.addCell(cell(String.valueOf(
                data.userStat().currentWeekUsers()), false));

        document.add(table);
    }

    // ================= RECENT BORROWS =================
    private void addRecentBorrowTable(Document document,
                                      List<BorrowRequestDetailDto> requests)
            throws DocumentException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        document.add(new Paragraph(
                "YÊU CẦU MƯỢN GẦN ĐÂY", roboto(14, true)));
        document.add(Chunk.NEWLINE);

        for (BorrowRequestDetailDto request : requests) {

            Paragraph info = new Paragraph();
            info.add(new Chunk("Người mượn: ", roboto(10, true)));
            info.add(new Chunk(request.borrowerName() + "\n", roboto(10, false)));

            info.add(new Chunk("Email: ", roboto(10, true)));
            info.add(new Chunk(request.borrowerEmail() + "\n", roboto(10, false)));

            info.add(new Chunk("SĐT: ", roboto(10, true)));
            info.add(new Chunk(request.borrowerPhone() + "\n", roboto(10, false)));

            info.add(new Chunk("Thời gian mượn: ", roboto(10, true)));
            info.add(new Chunk(
                    request.startDate().format(formatter)
                            + " - "
                            + request.endDate().format(formatter)
                            + "\n",
                    roboto(10, false)));

            info.add(new Chunk("Trạng thái: ", roboto(10, true)));
            info.add(new Chunk(request.status().name(), roboto(10, false)));

            document.add(info);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setSpacingAfter(15);

            header(table, "Tên sách");
            header(table, "Tác giả");
            header(table, "NXB");
            header(table, "Số lượng");
            header(table, "Từ ngày");
            header(table, "Đến ngày");

            for (BorrowRequestItemDto item : request.items()) {
                table.addCell(cell(item.bookTitle(), false));
                table.addCell(cell(item.bookAuthor(), false));
                table.addCell(cell(item.publisher(), false));
                table.addCell(cell(String.valueOf(item.quantity()), false));
                table.addCell(cell(item.dayStart().format(formatter), false));
                table.addCell(cell(item.dayEnd().format(formatter), false));
            }

            document.add(table);
        }
    }

    // ================= MONTHLY =================
    private void addMonthlyStatisticTable(Document document,
                                          DashboardExportDto data)
            throws DocumentException {

        document.add(new Paragraph(
                "THỐNG KÊ THEO THÁNG", roboto(14, true)));
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);

        header(table, "Tháng");
        header(table, "Lượt mượn");
        header(table, "Người dùng mới");

        for (int i = 0; i < 12; i++) {
            table.addCell(cell("Tháng " + (i + 1), false));
            table.addCell(cell(
                    String.valueOf(data.borrowCountByMonths().get(i)), false));
            table.addCell(cell(
                    String.valueOf(data.newUserCountByMonths().get(i)), false));
        }

        document.add(table);
    }

    // ================= CELL =================
    private PdfPCell cell(String text, boolean bold) {
        PdfPCell cell = new PdfPCell(
                new Phrase(text != null ? text : "", roboto(10, bold))
        );
        cell.setPadding(8);
        return cell;
    }

    private void header(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(
                new Phrase(text, roboto(10, true))
        );
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setPadding(8);
        table.addCell(cell);
    }

    // ================= FONT (UNICODE) =================
    private Font roboto(float size, boolean bold) {
        try {
            BaseFont baseFont = BaseFont.createFont(
                    getClass()
                            .getClassLoader()
                            .getResource("static/public/fonts/Roboto-Regular.ttf")
                            .toString(),
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
            );

            return new Font(
                    baseFont,
                    size,
                    bold ? Font.BOLD : Font.NORMAL
            );

        } catch (Exception e) {
            e.printStackTrace(); // 👈 BẮT BUỘC in ra
            throw new RuntimeException("Cannot load Roboto font", e);
        }
    }

}
