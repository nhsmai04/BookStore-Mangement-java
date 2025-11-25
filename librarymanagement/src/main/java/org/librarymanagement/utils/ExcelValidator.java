package org.librarymanagement.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.time.LocalDate;
import java.util.Set;
import java.util.regex.Pattern;

public class ExcelValidator {
    private static final String[] HEADERS = {
            "Tên sách", "Tác giả", "Đồng tác giả", "Thể loại", "Giới thiệu",
            "Nhà xuất bản", "Ngày xuất bản", "Số lượng", "Ảnh bìa"
    };

    public static List<String> validateExcelFile(InputStream inputStream) throws Exception {
        List<String> errors = new ArrayList<>();
        Set<String> bookNames = new HashSet<>();

        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        Row headerRow = sheet.getRow(0);
        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = headerRow.getCell(i);
            String value = cell != null ? cell.getStringCellValue().trim() : "";
            if (!HEADERS[i].equals(value)) {
                errors.add("Header sai ở cột " + (i + 1) + ": nên là '" + HEADERS[i] + "'");
            }
        }

        // Validate dữ liệu từng dòng
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null || isRowEmpty(row)) continue;

            for (int c = 0; c < HEADERS.length; c++) {
                Cell cell = row.getCell(c);
                String cellValue = cell != null ? cell.toString().trim() : "";

                switch (c) {
                    case 0: // Tên sách
                        if (cellValue.isEmpty()) {
                            errors.add("Dòng " + (r+1) + ", cột 'Tên sách' không được để trống");
                        } else {
                            // Check trùng tên
                            if (!bookNames.add(cellValue.toLowerCase())) {
                                errors.add("Dòng " + (r+1) + ", 'Tên sách' bị trùng: " + cellValue);
                            }
                            // Check chuỗi hợp lệ
                            if (isValidString(cellValue)) {
                                errors.add("Dòng " + (r+1) + ", 'Tên sách' chứa ký tự không hợp lệ");
                            }
                        }
                        break;
                    case 1: // Tác giả
                    case 3: // Thể loại
                    case 4: // Giới thiệu
                    case 5: // NXB
                        if (cellValue.isEmpty()) {
                            errors.add("Dòng " + (r+1) + ", cột '" + HEADERS[c] + "' không được để trống");
                        } else if (isValidString(cellValue)) {
                            errors.add("Dòng " + (r+1) + ", cột '" + HEADERS[c] + "' chứa ký tự không hợp lệ");
                        }
                        break;
                    case 2: // Đồng tác giả, có thể để trống
                        break;
                    case 6: // Ngày xuất bản
                        try {
                            LocalDate.parse(cellValue, dateFormatter);
                        } catch (Exception e) {
                            errors.add("Dòng " + (r+1) + ", cột 'Ngày xuất bản' sai định dạng, nên yyyy-MM-dd");
                        }
                        break;
                    case 7: // Số lượng
                        if (cell == null) {
                            errors.add("Dòng " + (r+1) + ", cột 'Số lượng' không được để trống");
                            break;
                        }
                        int qty;
                        if (cell.getCellType() == CellType.NUMERIC) {
                            double val = cell.getNumericCellValue();
                            if (val % 1 != 0) {
                                errors.add("Dòng " + (r+1) + ", cột 'Số lượng' phải là số nguyên");
                                continue;
                            }
                            qty = (int) val;
                        } else {
                            qty = Integer.parseInt(cell.getStringCellValue().trim());
                        }
                        if (qty <= 0) {
                            errors.add("Dòng " + (r+1) + ", cột 'Số lượng' phải > 0");
                        }
                        break;
                    case 8: // Ảnh bìa
                        if (cellValue.isEmpty()) errors.add("Dòng " + (r+1) + ", cột 'Ảnh bìa' không được để trống");
                        break;
                }
            }
        }

        workbook.close();
        return errors;
    }
    private static boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK && !cell.toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
    private static boolean isValidString(String input) {
        // Cho phép chữ, số, khoảng trắng và một số ký tự phổ biến
        return !Pattern.matches("^[\\p{L}0-9 ,.\\-()!?:;'\"]+$", input);
    }
}
