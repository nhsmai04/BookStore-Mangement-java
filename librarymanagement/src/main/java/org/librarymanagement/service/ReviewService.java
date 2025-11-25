package org.librarymanagement.service;

import org.librarymanagement.dto.request.ReviewRequest;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.dto.response.ReviewResponse;
import org.librarymanagement.entity.Book;
import org.librarymanagement.entity.Review;
import org.librarymanagement.entity.User;

public interface ReviewService {
    public ResponseObject addReview(String slug, User user, ReviewRequest reviewRequest);
}
