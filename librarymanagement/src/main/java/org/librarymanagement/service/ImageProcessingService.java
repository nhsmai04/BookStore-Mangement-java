package org.librarymanagement.service;
import org.opencv.core.*;

import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;

public interface ImageProcessingService {
    public Mat preprocessImage(BufferedImage bim)  throws Exception;
}
