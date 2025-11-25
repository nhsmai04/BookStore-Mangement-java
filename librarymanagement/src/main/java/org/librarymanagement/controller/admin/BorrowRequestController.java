package org.librarymanagement.controller.admin;

import org.librarymanagement.constant.ApiEndpoints;
import org.librarymanagement.constant.BRStatusConstant;
import org.librarymanagement.dto.response.BorrowRequestDetailDto;
import org.librarymanagement.dto.response.BorrowRequestSummaryDto;
import org.librarymanagement.dto.response.BorrowResponse;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.service.BorrowService;
import org.librarymanagement.service.CurrentUserService;
import org.librarymanagement.service.NotificationService;
import org.librarymanagement.service.PdfService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;

@Controller("adminBorrowRequestController")
@RequestMapping(ApiEndpoints.ADMIN_BORROW_REQUEST)
public class BorrowRequestController {
    private final BorrowService borrowService;
    private final PdfService pdfService;
    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;
    public BorrowRequestController(BorrowService borrowService,
                                   PdfService pdfService, NotificationService notificationService,
                                   CurrentUserService currentUserService) {
        this.borrowService = borrowService;
        this.pdfService = pdfService;
        this.notificationService = notificationService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public String showRequestBook(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model
    ) {
        Page<BorrowRequestSummaryDto> borrowRequests =
                borrowService.getAllRequests(status, PageRequest.of(page, size));

        model.addAttribute("borrowRequests", borrowRequests.getContent());
        model.addAttribute("totalPages", borrowRequests.getTotalPages() == 0 ? 1
                : borrowRequests.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("status", status);
        HashMap<Object, Object> BorrowRequestStatus;
        model.addAttribute("statuses", BRStatusConstant.values());
        return "admin/borrow-requests/index";
    }

    @GetMapping("/{id}")
    public String showRequestBookedit(@PathVariable Integer id, Model model) {
        BorrowRequestDetailDto detail = borrowService.getBorrowRequestDetail(id);
        model.addAttribute("detail", detail);
        return "admin/borrow-requests/detail";
    }

    @PatchMapping("/accept/{id}")
    public String acceptBorrowRequest(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        boolean success = borrowService.acceptBorrowRequest(id);
        if (success) {
            notificationService.notify(
                    "Phiếu mượn sách đã được chấp nhận",
                    "Phiếu mượn có id " + id + " đã được chấp nhận",
                    "BORROW_REQUEST_ACCEPTED",
                    currentUserService.getCurrentUser()

            );
            redirectAttributes.addFlashAttribute("message", "Phiếu mượn đã được xác nhận!");
            redirectAttributes.addFlashAttribute("alertType", "success");
        } else {
            redirectAttributes.addFlashAttribute("message", "Không tìm thấy phiếu mượn");
            redirectAttributes.addFlashAttribute("alertType", "danger");
        }
        // Chuyển hướng về trang danh sách phiếu mượn
        return "redirect:" + ApiEndpoints.ADMIN_BORROW_REQUEST;
    }

    @PostMapping("/upload")
    public String uploadBooks(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "No file uploaded. Please select a PDF file.");
            redirectAttributes.addFlashAttribute("alertType", "danger");
            return "redirect:" + ApiEndpoints.ADMIN_BOOK;
        }

        try {
            ResponseObject response = pdfService.uploadBooksFromPdf(file);
            if (response.status() == 200) {
                BorrowResponse borrowResponse = (BorrowResponse) response.data();
                notificationService.notify(
                        "Upload phiếu mượn thành công",
                        "Đã upload thành công  phiếu mươ từ file PDF.",
                        "BORROWREQUEST_UPLOADED",
                        currentUserService.getCurrentUser()
                );
                redirectAttributes.addFlashAttribute("message", "Upload successful!");
                redirectAttributes.addFlashAttribute("alertType", "success");
                redirectAttributes.addFlashAttribute("borrowResponse", borrowResponse);
            } else {
                System.out.println("[ERROR] Processing failed: " + response.message());
                redirectAttributes.addFlashAttribute("message", "Upload failed: " + response.message());
                redirectAttributes.addFlashAttribute("alertType", "danger");
            }
        } catch (Exception e) {
            System.out.println("[EXCEPTION] Upload failed: " + e.getMessage());
            redirectAttributes.addFlashAttribute("message", "Upload failed: " + e.getMessage());
            redirectAttributes.addFlashAttribute("alertType", "danger");
        }

        return "redirect:" + ApiEndpoints.ADMIN_BORROW_REQUEST;
    }
}
