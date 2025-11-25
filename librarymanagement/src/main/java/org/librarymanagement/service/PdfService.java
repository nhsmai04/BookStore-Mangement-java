package org.librarymanagement.service;

import org.librarymanagement.dto.response.ResponseObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PdfService {
    ResponseObject uploadBooksFromPdf(MultipartFile pdfFile) throws IOException;
}
