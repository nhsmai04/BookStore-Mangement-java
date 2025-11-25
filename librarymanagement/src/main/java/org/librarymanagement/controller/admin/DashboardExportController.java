package org.librarymanagement.controller.admin;

import jakarta.servlet.http.HttpServletResponse;
import org.librarymanagement.constant.ApiEndpoints;
import org.librarymanagement.service.DashboardExportService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(ApiEndpoints.ADMIN_DASHBOARD + "/export")
public class DashboardExportController {
    private  DashboardExportService dashboardExportService;

    public DashboardExportController(DashboardExportService dashBoardExportService) {
        this.dashboardExportService = dashBoardExportService;
    }

    @GetMapping("/pdf")
    public void exportPdf(HttpServletResponse response) throws Exception {
        dashboardExportService.exportPdf(response);
    }
}
