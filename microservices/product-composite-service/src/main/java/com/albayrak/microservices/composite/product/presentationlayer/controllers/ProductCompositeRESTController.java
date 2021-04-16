package com.albayrak.microservices.composite.product.presentationlayer.controllers;

import com.albayrak.api.composite.product.*;
import com.albayrak.api.core.product.Product;
import com.albayrak.api.core.recommendation.Recommendation;
import com.albayrak.api.core.review.Review;
import com.albayrak.microservices.composite.product.integrationlayer.ProductCompositeIntegration;
import com.albayrak.utils.exceptions.NotFoundException;
import com.albayrak.utils.http.ServiceUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ProductCompositeRESTController implements ProductCompositeServiceAPI {

    private final ServiceUtils serviceUtils;
    private ProductCompositeIntegration integration;

    public ProductCompositeRESTController(ServiceUtils serviceUtils, ProductCompositeIntegration integration) {
        this.serviceUtils = serviceUtils;
        this.integration = integration;
    }

    @Override
    public ProductAggregate getProduct(int productId) {

        Product product = integration.getProduct(productId);

        if(product == null) throw new NotFoundException("No product found for productId: " + productId);
        List<Recommendation> recommendations = integration.getRecommendations(productId);

        List<Review> reviews= integration.getReviews(productId);

        return createProductAggregate(product, recommendations, reviews, serviceUtils.getServiceAddress());

    }

    private ProductAggregate createProductAggregate(Product product, List<Recommendation> recommendations, List<Review> reviews, String serviceAddress) {

        //1. Setup product information
        int productId = product.getProductId();
        String name = product.getName();
        int weight = product.getWeight();

        //2. Copy summary recommendation info, if any
        List<RecommendationSummary> recommendationSummaries = (recommendations == null) ? null :
                recommendations.stream()
                        .map(recommendation -> new RecommendationSummary(recommendation.getRecommendationId(), recommendation.getAuthor(), recommendation.getRate()))
                        .collect(Collectors.toList());

        //3. Copy summary reviews info, if any
        List<ReviewSummary> reviewSummaries = (reviews == null) ? null :
                reviews.stream().map(review -> new ReviewSummary(review.getReviewId(), review.getAuthor(), review.getSubject()))
                        .collect(Collectors.toList());

        //4. Create info for microservice address
        String productAddress = product.getServiceAddress();
        String recommendationAddress = (recommendations != null && recommendations.size() > 0) ? recommendations.get(0).getServiceAddress() : "";
        String reviewsAddress = (reviews != null && reviews.size() > 0) ? reviews.get(0).getServiceAddress() : "";

        ServiceAddress serviceAddresses = new ServiceAddress(serviceAddress, productAddress, reviewsAddress, recommendationAddress);

        return new ProductAggregate(productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses);


    }
}
