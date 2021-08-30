package com.islam.microservices.composite.product.businesslayer;

import com.islam.api.composite.product.ProductAggregate;
import com.islam.api.composite.product.RecommendationSummary;
import com.islam.api.composite.product.ReviewSummary;
import com.islam.api.composite.product.ServiceAddress;
import com.islam.api.core.product.Product;
import com.islam.api.core.recommendation.Recommendation;
import com.islam.api.core.review.Review;
import com.islam.microservices.composite.product.integrationlayer.ProductCompositeIntegration;
import com.islam.utils.exceptions.NotFoundException;
import com.islam.utils.http.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductCompositeServiceImpl implements ProductCompositeService {

    private static  final Logger log = LoggerFactory.getLogger(ProductCompositeServiceImpl.class);

    private final ProductCompositeIntegration integration;
    private final ServiceUtils serviceUtils;

    public ProductCompositeServiceImpl(ProductCompositeIntegration integration, ServiceUtils serviceUtils) {
        this.integration = integration;
        this.serviceUtils = serviceUtils;
    }

    @Override
    public ProductAggregate getProduct(int productId) {

        Product product = integration.getProduct(productId);

        if(product == null) throw new NotFoundException("No product found for productId: " + productId);
        List<Recommendation> recommendations = integration.getRecommendations(productId);

        List<Review> reviews= integration.getReviews(productId);

        return createProductAggregate(product, recommendations, reviews, serviceUtils.getServiceAddress());
    }


    @Override
    public void createProduct(ProductAggregate model) {
        try{
            log.debug("createCompositeProduct: creates a new composite entity for productId: {}", model.getProductId());
            Product product = new Product(model.getProductId(), model.getName(), model.getWeight(), null);
            integration.createProduct(product);
            if (model.getRecommendations() != null){
                model.getRecommendations().forEach(r -> {Recommendation recommendation = new Recommendation(model.getProductId(), r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent(), null); integration.createRecommendation(recommendation);});
            }
            if (model.getReviews() != null){
                model.getReviews().forEach(r -> {Review review = new Review(model.getProductId(), r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent(), null); integration.createReview(review);});
            }

        }catch (RuntimeException ex){
            log.warn("createCompositeProduct failed", ex);
            throw ex;
        }
    }

    @Override
    public void deleteProduct(int productId) {

        log.debug("deleteCompositeProduct: Deletes a product aggregate for productId: {}", productId);

        integration.deleteProduct(productId);
        integration.deleteRecommendation(productId);
        integration.deleteReviews(productId);

        log.debug("deleteCompositeProduct: Deleted a product aggregate for productId: {}", productId);

    }
    private ProductAggregate createProductAggregate(Product product, List<Recommendation> recommendations, List<Review> reviews, String serviceAddress) {

        //1. Setup product information
        int productId = product.getProductId();
        String name = product.getName();
        int weight = product.getWeight();

        //2. Copy summary recommendation info, if any
        List<RecommendationSummary> recommendationSummaries = (recommendations == null) ? null :
                recommendations.stream()
                        .map(recommendation -> new RecommendationSummary(recommendation.getRecommendationId(), recommendation.getAuthor(), recommendation.getRate(), recommendation.getContent()))
                        .collect(Collectors.toList());

        //3. Copy summary reviews info, if any
        List<ReviewSummary> reviewSummaries = (reviews == null) ? null :
                reviews.stream().map(review -> new ReviewSummary(review.getReviewId(), review.getAuthor(), review.getSubject(), review.getContent()))
                        .collect(Collectors.toList());

        //4. Create info for microservice address
        String productAddress = product.getServiceAddress();
        String recommendationAddress = (recommendations != null && recommendations.size() > 0) ? recommendations.get(0).getServiceAddress() : "";
        String reviewsAddress = (reviews != null && reviews.size() > 0) ? reviews.get(0).getServiceAddress() : "";

        ServiceAddress serviceAddresses = new ServiceAddress(serviceAddress, productAddress, reviewsAddress, recommendationAddress);

        return new ProductAggregate(productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses);


    }
}
