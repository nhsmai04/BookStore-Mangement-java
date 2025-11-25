package org.librarymanagement.controller.admin;

import jakarta.servlet.Filter;
import org.librarymanagement.constant.ApiEndpoints;
import org.librarymanagement.dto.response.*;
import org.librarymanagement.service.BorrowRequestService;
import org.librarymanagement.service.DashBoardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;


@Controller
@RequestMapping(ApiEndpoints.ADMIN_DASHBOARD)
public class DashboardController {
    private DashBoardService dashBoardService;

    public DashboardController(DashBoardService dashBoardService) {
        this.dashBoardService = dashBoardService;
    }
    @GetMapping
    public String showDashboard(
            Model model,
            @RequestParam(value = "days", required = false) Integer days
    ) {
        // Lấy thống kê yêu cầu mượn sách theo tuần
        BorrowRequestStatDto borrowRequestStatDto = dashBoardService.getBorrowRequestStat();
        model.addAttribute("borrowRequestStat", borrowRequestStatDto);

        // Lấy thống kê sách theo tháng
        BookStatDto bookStatDto = dashBoardService.getBookStat();
        model.addAttribute("bookStat", bookStatDto);

        // Lây thống kế sách mượn theo tuần
        BookRequestStatDto bookRequestStatDto = dashBoardService.getBookRequestStat();
        model.addAttribute("bookRequestStat", bookRequestStatDto);

        // Lấy thống kế người dùng theo tuần
        UserStatDto userStatDto = dashBoardService.getUserStat();
        model.addAttribute("userStat", userStatDto);

        // Lấy thống kế các yêu cầu mượn sách gần đây
        // truyền lại days để giữ trạng thái select
        model.addAttribute("days", days);
        Integer limit = (days != null) ? days : 0;
        List<BorrowRequestDetailDto> items = dashBoardService.getRecentBorrowRequests(limit) ;
        model.addAttribute("items", items);

        // Lấy thông tin tổng lượt mượn thống kế theo tháng cho bảng chart
        Integer year = LocalDate.now().getYear();
        model.addAttribute("year", year);
        List<Long> borrowCountByMonths = dashBoardService.getMonthlyBorrowCounts(year);
        model.addAttribute("borrowData", borrowCountByMonths);

        //Lấy thông tin người dùng mới theo năm cho bảng chart
        List<Long> newUserCountByMonths = dashBoardService.getMonthlyNewUserCounts(year);
        model.addAttribute("memberData", newUserCountByMonths);
        return "admin/dashboard"; // Thymeleaf template

    }

}
