package com.islam.microservices.core.recommendation.presentationlayer.controllers;

import com.islam.api.core.product.Product;
import com.islam.api.core.recommendation.Recommendation;
import com.islam.api.core.recommendation.RecommendationServiceAPI;
import com.islam.microservices.core.recommendation.businesslayer.RecommendationService;
import com.islam.utils.exceptions.InvalidInputException;
import com.islam.utils.http.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RecommendationRESTController implements RecommendationServiceAPI {

    private static final Logger LOG = LoggerFactory.getLogger(RecommendationRESTController.class);
    private final RecommendationService recommendationService;

    public RecommendationRESTController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {

        if(productId < 1) throw new InvalidInputException("Invalid productId: " + productId);
     /*   if(productId == 113) {
            LOG.debug("No recommendations found for productId: {}", + productId);
            return new ArrayList<>();
        }*/
            List<Recommendation> recommendations = recommendationService.getProductById(productId);
            return recommendations;
    }

    @Override
    public Recommendation createRecommendation(Recommendation model) {
        Recommendation recommendation = recommendationService.createRecommendation(model);
        LOG.debug("REST createRecommendation: recommendation created for productId: {}", recommendation.getProductId());
        return recommendation;
    }

    @Override
    public void deleteRecommendation(int productId) {

        LOG.debug("deleteRecommendation: trying to delete all recommendation entity for productId: {}", productId);
        recommendationService.deleteRecommendations(productId);

    }
}
