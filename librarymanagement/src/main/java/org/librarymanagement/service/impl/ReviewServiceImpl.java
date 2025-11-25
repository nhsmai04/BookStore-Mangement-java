package org.librarymanagement.service.impl;

import org.librarymanagement.constant.BRItemStatusConstant;
import org.librarymanagement.dto.request.ReviewRequest;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.dto.response.ReviewResponse;
import org.librarymanagement.dto.response.UserResponse;
import org.librarymanagement.entity.*;
import org.librarymanagement.exception.DuplicateFieldException;
import org.librarymanagement.exception.NotBorrowedException;
import org.librarymanagement.exception.NotFoundException;
import org.librarymanagement.repository.*;
import org.librarymanagement.service.ReviewService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional(readOnly=true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final BorrowRequestItemRepository borrowRequestItemRepository;
    private final MessageSource messageSource;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             BookRepository bookRepository,
                             BorrowRequestItemRepository borrowRequestItemRepository,
                             MessageSource messageSource
    ) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.borrowRequestItemRepository = borrowRequestItemRepository;
        this.messageSource = messageSource;
    }

    @Override
    @Transactional
    public ResponseObject addReview(String slug, User user, ReviewRequest reviewRequest) {

        Book book = bookRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sách với slug: " + slug));

        checkIfBorrowed(book, user);

        checkIfAlreadyReviewed(book, user);

        Review review = new Review();
        review.setBook(book);
        review.setComment(reviewRequest.getComment());
        review.setStar(reviewRequest.getStar());
        review.setUser(user);

        Review savedReview = reviewRepository.save(review);

        UserResponse userResponse = new UserResponse(
                savedReview.getUser().getId(),
                savedReview.getUser().getName(),
                savedReview.getUser().getUsername()
        );

        ReviewResponse reviewResponse = new ReviewResponse(
                savedReview.getId(),
                savedReview.getComment(),
                savedReview.getStar(),
                savedReview.getCreatedAt(),
                userResponse
        );

        String successMessage = messageSource.getMessage(
                "review.create.success",
                null,
                LocaleContextHolder.getLocale()
        );

        return new ResponseObject(
                successMessage,
                201,
                reviewResponse
        );
    }

    private void checkIfBorrowed(Book book, User user) {

        boolean hasBorrowed = borrowRequestItemRepository.existsByBookAndUserAndStatus(book, user, BRItemStatusConstant.RETURNED);

        String notBorrowedMessage = messageSource.getMessage(
                "review.create.notBorrowed",
                null,
                LocaleContextHolder.getLocale()
        );

        if (!hasBorrowed) {
            throw new NotBorrowedException(notBorrowedMessage);
        }
    }

    private void checkIfAlreadyReviewed(Book book, User user){

        reviewRepository.findByBookAndUser(book, user)
                .ifPresent(review -> {

                    String hasAlreadyReviewedMessage = messageSource.getMessage(
                            "review.create.hasReviewed",
                            null,
                            LocaleContextHolder.getLocale()
                    );

                    Map<String, String> fieldErrors = Map.of("review", hasAlreadyReviewedMessage);
                    throw new DuplicateFieldException(fieldErrors);
                });
    }
}
