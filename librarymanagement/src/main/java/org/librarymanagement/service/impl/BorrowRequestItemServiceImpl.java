package org.librarymanagement.service.impl;

import jakarta.transaction.Transactional;
import org.librarymanagement.constant.BRItemStatusConstant;
import org.librarymanagement.constant.BookVersionConstants;
import org.librarymanagement.entity.BookVersion;
import org.librarymanagement.entity.BorrowRequest;
import org.librarymanagement.entity.BorrowRequestItem;
import org.librarymanagement.exception.NotFoundException;
import org.librarymanagement.repository.BorrowRequestItemRepository;
import org.librarymanagement.repository.BorrowRequestRepository;
import org.librarymanagement.service.BorrowRequestItemService;
import org.librarymanagement.service.BorrowRequestService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BorrowRequestItemServiceImpl implements BorrowRequestItemService {
    private final BorrowRequestItemRepository borrowRequestItemRepository;
    private final BorrowRequestRepository  borrowRequestRepository;

    public BorrowRequestItemServiceImpl(BorrowRequestItemRepository borrowRequestItemRepository,  BorrowRequestRepository borrowRequestRepository) {
        this.borrowRequestItemRepository = borrowRequestItemRepository;
        this.borrowRequestRepository = borrowRequestRepository;
    }

    @Transactional
    public void returnRequestItem(Integer itemId) {
        BorrowRequestItem borrowRequestItem = borrowRequestItemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Không tìm thấy phiếu mượn với id: " + itemId));
        borrowRequestItem.setStatus(BRItemStatusConstant.RETURNED);
        BookVersion bookVersion = borrowRequestItem.getBookVersion();
        bookVersion.setStatus(BookVersionConstants.AVAILABLE);
        borrowRequestItem.setBookVersion(bookVersion);
        borrowRequestItemRepository.save(borrowRequestItem);
    }

    public boolean checkAlreadyReturnedBRItem(Integer borrowId) {
        BorrowRequest borrowRequest = borrowRequestRepository.findById(borrowId).orElseThrow(() -> new NotFoundException("Khong tim thay phieu muon"));
        List<BorrowRequestItem> borrowRequestItems = borrowRequestItemRepository.findBorrowRequestItemByBorrowRequest(borrowRequest).stream().filter(b -> b.getStatus().equals(BRItemStatusConstant.RETURNED)).toList();
        System.out.println(borrowRequestItems.size());
        System.out.println(borrowRequest.getQuantity());
        if(borrowRequestItems.size()  == borrowRequest.getQuantity()) {
            return true;
        }
        return false;
    }
}