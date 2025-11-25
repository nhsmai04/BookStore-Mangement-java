package org.librarymanagement.service;

import org.librarymanagement.dto.response.*;

import java.util.List;

public interface DashBoardService {
BorrowRequestStatDto getBorrowRequestStat();
BookStatDto getBookStat();
BookRequestStatDto getBookRequestStat();
UserStatDto getUserStat();
List<BorrowRequestDetailDto> getRecentBorrowRequests(int limit);
List<Long> getMonthlyBorrowCounts(int year);
List<Long> getMonthlyNewUserCounts(int year);
}
