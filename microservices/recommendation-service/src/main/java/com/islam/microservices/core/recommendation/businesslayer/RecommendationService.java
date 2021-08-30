package com.islam.microservices.core.recommendation.businesslayer;

import com.islam.api.core.recommendation.Recommendation;

import java.util.List;

public interface RecommendationService {

    public List<Recommendation> getProductById(int productId);

    public Recommendation createRecommendation(Recommendation model);

    public void deleteRecommendations(int productId);

}
