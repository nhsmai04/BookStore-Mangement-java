package org.librarymanagement.controller.api;

import jakarta.validation.Valid;
import org.librarymanagement.constant.ApiEndpoints;
import org.librarymanagement.dto.request.ReviewRequest;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.entity.User;
import org.librarymanagement.service.CurrentUserService;
import org.librarymanagement.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiEndpoints.USER_BOOK)
public class ReviewController {

    private final ReviewService reviewService;
    private final CurrentUserService currentUserService;

    @Autowired
    public ReviewController(ReviewService reviewService, CurrentUserService currentUserService) {
        this.reviewService = reviewService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/{slug}/reviews")
    public ResponseEntity<ResponseObject> addReview(@PathVariable String slug,
                                                    @Valid @RequestBody ReviewRequest reviewRequest) {

        User user = currentUserService.getCurrentUser();

        ResponseObject responseObject = reviewService.addReview(slug, user, reviewRequest);

        return ResponseEntity.status(responseObject.status())
                .body(responseObject);
    }
}
