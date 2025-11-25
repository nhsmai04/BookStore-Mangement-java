package org.librarymanagement.service.impl;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.librarymanagement.service.OCRService;
import org.opencv.core.Mat;

import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class OCRServiceImpl implements OCRService {
    private final ITesseract tesseract;

    public OCRServiceImpl() {
        // Khởi tạo Tesseract một lần duy nhất
        this.tesseract = new Tesseract();
        this.tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
    }

    public String performOCR(Mat image) throws Exception {
        // Sửa tên hàm gọi thành matToBufferedImage
        BufferedImage bufferedImage = matToBufferedImage(image);

        try {
            return tesseract.doOCR(bufferedImage);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error during OCR: " + e.getMessage();
        }
    }

    private BufferedImage matToBufferedImage(Mat mat) {
        int width = mat.width();
        int height = mat.height();
        int channels = mat.channels();
        byte[] data = new byte[width * height * channels];
        mat.get(0, 0, data);

        BufferedImage bufferedImage;
        if (channels == 1) {
            // Nếu là ảnh xám hoặc ảnh binary (kết quả của threshold)
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        } else {
            // Nếu là ảnh màu
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        }

        bufferedImage.getRaster().setDataElements(0, 0, width, height, data);
        return bufferedImage;
    }
}
