package com.islam.microservices.core.recommendation.businesslayer;

import com.islam.api.core.product.Product;
import com.islam.api.core.recommendation.Recommendation;
import com.islam.microservices.core.recommendation.datalayer.RecommendationEntity;
import com.islam.microservices.core.recommendation.datalayer.RecommendationRepository;
import com.islam.utils.exceptions.InvalidInputException;
import com.islam.utils.exceptions.NotFoundException;
import com.islam.utils.http.ServiceUtils;
import com.mongodb.DuplicateKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService{

    private static final Logger LOG = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    private final RecommendationRepository repository;

    private final RecommendationMapper mapper;

    private ServiceUtils serviceUtils;

    public RecommendationServiceImpl(RecommendationRepository repository, RecommendationMapper mapper, ServiceUtils serviceUtils) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtils = serviceUtils;
    }

    @Override
    public List<Recommendation> getProductById(int productId) {
        List<RecommendationEntity> entityList = repository.findByProductId(productId);
        List<Recommendation> list = mapper.entityListToModelList(entityList);

        LOG.debug("Recommendation getByProductId: found {} recommendations", list.size());
        return list;
    }

    @Override
    public Recommendation createRecommendation(Recommendation model) {
            RecommendationEntity entity = mapper.modelToEntity(model);
            RecommendationEntity newEntity = repository.save(entity);

            LOG.debug("RecommendationService createRecommendation: recommendation entity created: {}/{}", model.getProductId(), model.getRecommendationId());

            return mapper.entityToModel(newEntity);
    }

    @Override
    public void deleteRecommendations(int productId) {

        LOG.debug("RecommendationService deleteRecommendation: trying to delete all recommendation entity for productId: {}", productId);
        repository.deleteAll(repository.findByProductId(productId));

    }
}
