package org.librarymanagement.service;

public interface BorrowRequestItemService {
    void returnRequestItem(Integer itemId);
    boolean checkAlreadyReturnedBRItem(Integer borrowId);
}