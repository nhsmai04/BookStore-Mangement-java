package org.librarymanagement.service;

import org.opencv.core.Mat;

public interface OCRService {
    public String performOCR(Mat image) throws Exception;
}
