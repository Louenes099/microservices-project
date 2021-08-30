package com.islam.api.core.review;

import com.islam.api.core.recommendation.Recommendation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface ReviewServiceAPI {

    @GetMapping(
            value = "/review",
            produces = "application/json"
    )
    List<Review> getReviews(@RequestParam(value = "productId", required = true)int productId);

    @PostMapping(value = "/review", consumes = "application/json", produces = "application/json")
    Review createReview(@RequestBody Review model);

    @DeleteMapping(value = "/review")
    void deleteReviews(@RequestParam(value = "productId", required = true)  int productId);
}
