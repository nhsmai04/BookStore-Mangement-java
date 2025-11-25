package org.librarymanagement.service.impl;

import org.librarymanagement.service.ImageProcessingService;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

@Service
public class ImageProcessingServiceImpl implements ImageProcessingService {


    public Mat preprocessImage(BufferedImage bim) throws Exception {

        Mat image = bufferedImageToMat(bim);
        // 1. Chuyển sang ảnh xám
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // 2. Làm sạch nhiễu (Threshold) -> Kết quả là ảnh 1 kênh (Binary)
        Mat binary = new Mat();
        Imgproc.threshold(gray, binary, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

        // Giải phóng bộ nhớ cho các Mat trung gian
        image.release();
        gray.release();

        return binary;
    }
    private Mat bufferedImageToMat(BufferedImage bi) {
        // Ép kiểu ảnh về BGR để đảm bảo luôn có 3 kênh, tránh lỗi định dạng lạ (như PNG có kênh Alpha)
        BufferedImage convertedImg = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        convertedImg.getGraphics().drawImage(bi, 0, 0, null);

        byte[] pixels = ((DataBufferByte) convertedImg.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, pixels);
        return mat;
    }
}
