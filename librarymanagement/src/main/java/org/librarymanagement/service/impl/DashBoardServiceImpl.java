package org.librarymanagement.service.impl;

import org.librarymanagement.dto.response.*;
import org.librarymanagement.entity.BorrowRequest;
import org.librarymanagement.repository.BookRepository;
import org.librarymanagement.repository.BorrowRequestRepository;
import org.librarymanagement.repository.UserRepository;
import org.librarymanagement.service.BorrowService;
import org.librarymanagement.service.DashBoardService;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class
DashBoardServiceImpl implements DashBoardService {
    private final BorrowRequestRepository borrowRequestRepository;
    private final BookRepository bookRepository;
    private final BorrowService borrowService;
    private final UserRepository UserRepository;

    public DashBoardServiceImpl(BorrowRequestRepository borrowRequestRepository, BookRepository bookRepository,
                                BorrowService borrowService, UserRepository UserRepository) {
        this.borrowRequestRepository = borrowRequestRepository;
        this.bookRepository = bookRepository;
        this.borrowService = borrowService;
        this.UserRepository = UserRepository;
    }

    private Double calculatePercentChange(Integer currentWeekCount, Integer lastWeekCount) {
        if (lastWeekCount == 0) {
            return currentWeekCount == 0 ? 0.0 : 100.0;
        }
        return ((double) (currentWeekCount - lastWeekCount) / lastWeekCount) * 100;
    }

    @Override
    public BorrowRequestStatDto getBorrowRequestStat() {

        LocalDateTime startOfCurrentWeek = LocalDateTime.now()
                .with(DayOfWeek.MONDAY)
                .toLocalDate().atStartOfDay();
        System.out.println("Current Week Start: " + startOfCurrentWeek);
        LocalDateTime endofCurrentWeek = LocalDateTime.now();
        System.out.println("Current Week End: " + endofCurrentWeek);
        Integer currentWeekBorrowRequests = borrowRequestRepository.countBorrowRequestsByDayConfirmed(startOfCurrentWeek, endofCurrentWeek);

        //Tuan truoc
        LocalDateTime StartOfLastWeek = startOfCurrentWeek.minusWeeks(1);
        System.out.println("Last Week Start: " + StartOfLastWeek);
        LocalDateTime endOfLastWeek = startOfCurrentWeek.minusSeconds(1);
        System.out.println("Last Week End: " + endOfLastWeek);
        Integer lastWeekBorrowRequests = borrowRequestRepository.countBorrowRequestsByDayConfirmed(StartOfLastWeek, endOfLastWeek);

        if (currentWeekBorrowRequests == null) {
            currentWeekBorrowRequests = 0;
        }
        if (lastWeekBorrowRequests == null) {
            lastWeekBorrowRequests = 0;
        }
        Double percentChange = calculatePercentChange(currentWeekBorrowRequests, lastWeekBorrowRequests);
        return new BorrowRequestStatDto(
                currentWeekBorrowRequests,
                lastWeekBorrowRequests,
                percentChange
        );
    }

    @Override
    public BookStatDto getBookStat() {
        // Tháng này
        LocalDateTime startOfThisMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfThisMonth = LocalDateTime.now(); // Hôm nay
        Integer thisMonthBooks = bookRepository.countBooksByCreatedAt(startOfThisMonth, endOfThisMonth);

        // Tháng trước
        LocalDateTime startOfLastMonth = startOfThisMonth.minusMonths(1);
        LocalDateTime endOfLastMonth = startOfThisMonth.minusSeconds(1);
        Integer lastMonthBooks = bookRepository.countBooksByCreatedAt(startOfLastMonth, endOfLastMonth);

        if (thisMonthBooks == null) {
            thisMonthBooks = 0;
        }
        if (lastMonthBooks == null) {
            lastMonthBooks = 0;
        }
        double percentChange = calculatePercentChange(thisMonthBooks, lastMonthBooks);
        return new BookStatDto(
                thisMonthBooks,
                lastMonthBooks,
                percentChange
        );
    }

    @Override
    public BookRequestStatDto getBookRequestStat() {
        LocalDateTime startOfCurrentWeek = LocalDateTime.now()
                .with(DayOfWeek.MONDAY)
                .toLocalDate().atStartOfDay();
        System.out.println("Current Week Start: " + startOfCurrentWeek);
        LocalDateTime endofCurrentWeek = LocalDateTime.now();
        System.out.println("Current Week End: " + endofCurrentWeek);
        Integer currentWeekBorrowRequests = borrowRequestRepository.sumQuantityByDayConfirmed(startOfCurrentWeek, endofCurrentWeek);

        //Tuan truoc
        LocalDateTime StartOfLastWeek = startOfCurrentWeek.minusWeeks(1);
        System.out.println("Last Week Start: " + StartOfLastWeek);
        LocalDateTime endOfLastWeek = startOfCurrentWeek.minusSeconds(1);
        System.out.println("Last Week End: " + endOfLastWeek);
        Integer lastWeekBorrowRequests = borrowRequestRepository.sumQuantityByDayConfirmed(StartOfLastWeek, endOfLastWeek);

        if (currentWeekBorrowRequests == null) {
            currentWeekBorrowRequests = 0;
        }
        if (lastWeekBorrowRequests == null) {
            lastWeekBorrowRequests = 0;
        }
        Double percentChange = calculatePercentChange(currentWeekBorrowRequests, lastWeekBorrowRequests);
        return new BookRequestStatDto(
                currentWeekBorrowRequests,
                lastWeekBorrowRequests,
                percentChange
        );
    }
    public UserStatDto getUserStat()
    {
        LocalDateTime startOfCurrentWeek = LocalDateTime.now()
                .with(DayOfWeek.MONDAY)
                .toLocalDate().atStartOfDay();
        System.out.println("Current Week Start: " + startOfCurrentWeek);
        LocalDateTime endofCurrentWeek = LocalDateTime.now();
        System.out.println("Current Week End: " + endofCurrentWeek);
        Integer currentWeekUsers = UserRepository.countNewUsersByDay(startOfCurrentWeek, endofCurrentWeek);

        //Tuan truoc
        LocalDateTime StartOfLastWeek = startOfCurrentWeek.minusWeeks(1);
        System.out.println("Last Week Start: " + StartOfLastWeek);
        LocalDateTime endOfLastWeek = startOfCurrentWeek.minusSeconds(1);
        System.out.println("Last Week End: " + endOfLastWeek);
        Integer lastWeekUsers = UserRepository.countNewUsersByDay(StartOfLastWeek, endOfLastWeek);

        if (currentWeekUsers == null) {
            currentWeekUsers = 0;
        }
        if (lastWeekUsers == null) {
            lastWeekUsers = 0;
        }
        Double percentChange = calculatePercentChange(currentWeekUsers, lastWeekUsers);
        return new UserStatDto(
                currentWeekUsers,
                lastWeekUsers,
                percentChange
        );
    }


    public List<BorrowRequestDetailDto> getRecentBorrowRequests(int limit)
    {
        List<BorrowRequest> borrowRequests;
        if(limit == 0)
        {
            borrowRequests = borrowRequestRepository.findAll();
        }
        else{
            LocalDateTime dayEnd = LocalDateTime.now();
            LocalDateTime dayStart = dayEnd.minusDays(limit);
            borrowRequests = borrowRequestRepository.findAllByDay(dayStart, dayEnd);
        }


        return borrowRequests.stream()
                .map(borrowService::convertToBorrowRequestDetailDto)
                .toList();
    }

    public  List<Long> getMonthlyBorrowCounts(int year)
    {
        List<BorrowCountByMonth> data = borrowRequestRepository.countBorrowRequestsGroupedByMonth(year);
        Map<Integer,Long> map = new HashMap<>();
        for(BorrowCountByMonth row : data)
        {
            map.put(row.getMonth(), row.getBorrowCount());
        }
        List<Long> result = new ArrayList<>();
        for(int month =1; month <=12; month++)
        {
            result.add(map.getOrDefault(month, 0L));
        }
        return result;
    }
    public List<Long> getMonthlyNewUserCounts(int year)
    {
        List<UserCountByMonth> data = UserRepository.countNewUsersGroupedByMonth(year);
        Map<Integer,Long> map = new HashMap<>();
        for(UserCountByMonth row : data)
        {
            map.put(row.getMonth(), row.getUserCount());
        }
        List<Long> result = new ArrayList<>();
        for(int month =1; month <=12; month++)
        {
            result.add(map.getOrDefault(month, 0L));
        }
        return result;
    }
}
