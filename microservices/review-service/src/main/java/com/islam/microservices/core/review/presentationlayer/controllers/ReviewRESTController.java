package com.islam.microservices.core.review.presentationlayer.controllers;

import com.islam.api.core.review.Review;
import com.islam.api.core.review.ReviewServiceAPI;
import com.islam.microservices.core.review.businesslayer.ReviewService;
import com.islam.utils.exceptions.InvalidInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class ReviewRESTController implements ReviewServiceAPI {
    private static final Logger LOG = LoggerFactory.getLogger(ReviewRESTController.class);
    private final ReviewService reviewService;

    public ReviewRESTController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Override
    public List<Review> getReviews(int productId) {
        if(productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

//        if(productId == 213){
//            LOG.debug("No review found for productid: {}", productId);
//            return new ArrayList<>();
//        }
        List<Review> listReviews = reviewService.getProductById(productId);
//        listReviews.add(reviewService.getReviewById(productId));
//        listReviews.add(new Review(productId, 2, "Author 2", "Subject 2","Content 2", serviceUtil.getServiceAddress()));
//        listReviews.add(new Review(productId, 3, "Author 3", "Subject 3","Content 3", serviceUtil.getServiceAddress()));

        LOG.debug("/review found response size: {}", listReviews.size());
        return listReviews;
    }

    @Override
    public Review createReview(Review model) {
        Review review = reviewService.createReview(model);
        LOG.debug("REST createRecommendation: review created for productId: {}",review.getProductId());
        return review;
    }

    @Override
    public void deleteReviews(int productId) {
        reviewService.deleteReviews(productId);

        LOG.debug("REST deleteReview: tried to deleted productId: {}",productId);
    }
}