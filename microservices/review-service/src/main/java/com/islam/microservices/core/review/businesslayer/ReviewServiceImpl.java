package com.islam.microservices.core.review.businesslayer;

import com.islam.api.core.recommendation.Recommendation;
import com.islam.api.core.review.Review;
import com.islam.microservices.core.review.datalayer.ReviewEntity;
import com.islam.microservices.core.review.datalayer.ReviewRepository;
import com.islam.utils.http.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewRepository repository;

    private final ReviewMapper mapper;

    public ReviewServiceImpl(ReviewRepository repository, ReviewMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<Review> getProductById(int productId) {
        List<ReviewEntity> entityList = repository.findByProductId(productId);
        List<Review> list = mapper.entityListToModelList(entityList);

        LOG.debug("Recommendation getByProductId: found {} recommendations", list.size());
        return list;
    }

    @Override
    public Review createReview(Review model) {
        ReviewEntity entity = mapper.modelToEntity(model);
        ReviewEntity newEntity = repository.save(entity);

        LOG.debug("RecommendationService createRecommendation: recommendation entity created: {}/{}", model.getProductId(), model.getReviewId());

        return mapper.entityToModel(newEntity);
    }

    @Override
    public void deleteReviews(int productId) {

        LOG.debug("RecommendationService deleteRecommendation: trying to delete all recommendation entity for productId: {}", productId);
        repository.deleteAll(repository.findByProductId(productId));

    }
}
