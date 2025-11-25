package org.librarymanagement.service;

import jakarta.servlet.http.HttpServletResponse;
import org.librarymanagement.dto.response.DashboardExportDto;

public interface PdfExportService {
void exportDashBoard(HttpServletResponse response, DashboardExportDto data);
}
