package org.librarymanagement.service;

import jakarta.servlet.http.HttpServletResponse;
import org.librarymanagement.dto.response.DashboardExportDto;

public interface DashboardExportService {

    void exportPdf(HttpServletResponse response);
}
