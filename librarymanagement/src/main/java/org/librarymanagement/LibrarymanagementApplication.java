package org.librarymanagement;


import org.bytedeco.javacpp.Loader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LibrarymanagementApplication {

    static {
        // Nạp thư viện native của OpenCV
        Loader.load(org.bytedeco.opencv.opencv_java.class);
    }

    public static void main(String[] args) {

        SpringApplication.run(LibrarymanagementApplication.class, args);
    }

}
