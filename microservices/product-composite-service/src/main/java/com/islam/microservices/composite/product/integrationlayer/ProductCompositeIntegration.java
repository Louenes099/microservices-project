package com.islam.microservices.composite.product.integrationlayer;

import com.islam.api.core.product.Product;
import com.islam.api.core.product.ProductServiceAPI;
import com.islam.api.core.recommendation.Recommendation;
import com.islam.api.core.recommendation.RecommendationServiceAPI;
import com.islam.api.core.review.Review;
import com.islam.api.core.review.ReviewServiceAPI;
import com.islam.utils.exceptions.InvalidInputException;
import com.islam.utils.exceptions.NotFoundException;
import com.islam.utils.http.HttpErrorInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductCompositeIntegration implements ProductServiceAPI, RecommendationServiceAPI, ReviewServiceAPI {


    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String productServiceUrl;
    private final String recommendationServiceUrl;
    private final String reviewServiceUrl;


    public ProductCompositeIntegration(

            RestTemplate restTemplate,
            ObjectMapper mapper,

            @Value("${app.product-service.host}") String productServiceHost,
            @Value("${app.product-service.port}") String productServicePort,

            @Value("${app.recommendation-service.host}") String recommendationServiceHost,
            @Value("${app.recommendation-service.port}") String recommendationServicePort,

            @Value("${app.review-service.host}") String reviewServiceHost,
            @Value("${app.review-service.port}") String reviewServicePort
    ) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;

        //productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/product/";
        productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/product";
        //recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation?productId=";
        recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation";
        //reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review?productId=";
        reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review";


    }

    @Override
    public Product getProduct(int productId) {
        try{
            String url = productServiceUrl +"/"+ productId;
            LOG.debug("Will call getProduct API on URL: {}", url);

            Product product = restTemplate.getForObject(url, Product.class);
            LOG.debug("Found a product with id: {}", product.getProductId());

            return product;
        }
        catch (HttpClientErrorException ex){ //since we are the API client, we need to handle HTTP errors
            throw handleHttpClientException(ex);
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex){
        switch (ex.getStatusCode()){
            case NOT_FOUND:
                throw new NotFoundException(getErrorMessage(ex));
            case UNPROCESSABLE_ENTITY:
                throw new InvalidInputException(getErrorMessage(ex));
            default:
                LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusText());
                LOG.warn("Error body: {}", ex.getResponseBodyAsString());
                throw ex;
        }
    }

    @Override
    public Product createProduct(Product model) {
        try{
            String url = productServiceUrl;
            LOG.debug("Will call createProduct API on URL: {}", url);
            return restTemplate.postForObject(productServiceUrl, model, Product.class);
        }
        catch (HttpClientErrorException ex){ //since we are the API client, we need to handle HTTP errors
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteProduct(int productId) {
        try{
            String url = productServiceUrl+"/" + productId;
            LOG.debug("Will call deleteProduct API on URL: {}", url);
            restTemplate.delete(url);
        }
        catch (HttpClientErrorException ex){ //since we are the API client, we need to handle HTTP errors
            throw handleHttpClientException(ex);
        }

    }

    private String getErrorMessage(HttpClientErrorException ex) {

        try{
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();

        }catch(IOException ioex){
            return ioex.getMessage();

        }
    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {
        try{
            String url = recommendationServiceUrl+"/" + "?productId=" + productId;

            LOG.debug("Will call getRecommendations API on URL: {}", url);
            List<Recommendation> recommendations = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Recommendation>>() {
                    }).getBody();
            LOG.debug("Found {} recommendations for a product with id: {}", recommendations.size(), productId);
            return recommendations;
        }catch (Exception ex){
            LOG.warn("Got an exception while requesting recommendations, return zero recommendations: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Recommendation createRecommendation(Recommendation model) {

        try{
            String url = recommendationServiceUrl;
            LOG.debug("Will call createRecommendation API on URL: {}", url);
            Recommendation recommendation = restTemplate.postForObject(recommendationServiceUrl, model, Recommendation.class);
            LOG.debug("Created a recommendation with productId: {} and recommendationId: {}",recommendation.getProductId(), recommendation.getRecommendationId());
            return recommendation;
        }
        catch (HttpClientErrorException ex){ //since we are the API client, we need to handle HTTP errors
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteRecommendation(int productId) {
        try{
            String url = recommendationServiceUrl +"/"+ "?productId=" + productId;
            LOG.debug("Will call deleteRecommendations API on URL: {}", url);
            restTemplate.delete(url);
        }
        catch (HttpClientErrorException ex){ //since we are the API client, we need to handle HTTP errors
            throw handleHttpClientException(ex);
        }

    }

    @Override
    public List<Review> getReviews(int productId) {

        try{
            String url = reviewServiceUrl +"/"+ "?productId=" + productId;


        LOG.debug("Will call getReview API on URL: {}", url);
        List<Review> reviews = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Review>>() {
                }).getBody();

        LOG.debug("Found {} reviews for a product with id: {}", reviews.size(), productId);
        return reviews;
    }catch(Exception ex){
        LOG.warn("Got an exception while requesting reviews, will return zero reviews: {}", ex.getMessage());
        return new ArrayList<>();
    }
}

    @Override
    public Review createReview(Review model) {
        try{
            String url = reviewServiceUrl;
            LOG.debug("Will call createReview API on URL: {}", url);
            Review review = restTemplate.postForObject(reviewServiceUrl, model, Review.class);
            LOG.debug("Created a review with productId: {} and reviewId: {}",review.getProductId(), review.getReviewId());
            return review;
        }
        catch (HttpClientErrorException ex){ //since we are the API client, we need to handle HTTP errors
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteReviews(int productId) {
        try{
            String url = reviewServiceUrl +"/"+ "?productId=" + productId;
            LOG.debug("Will call deleteReview API on URL: {}", url);
            restTemplate.delete(url);
        }
        catch (HttpClientErrorException ex){ //since we are the API client, we need to handle HTTP errors
            throw handleHttpClientException(ex);
        }

    }
}
