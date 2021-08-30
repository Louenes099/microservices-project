package com.islam.microservices.core.recommendation.businesslayer;

import com.islam.api.core.recommendation.Recommendation;
import com.islam.microservices.core.recommendation.datalayer.RecommendationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

    @Mapping(target = "serviceAddress", ignore = true)
    Recommendation entityToModel(RecommendationEntity entity);

    @Mappings({@Mapping(target = "id", ignore = true), @Mapping(target = "version", ignore = true)})
    RecommendationEntity modelToEntity(Recommendation model);

    List<Recommendation> entityListToModelList(List<RecommendationEntity> entity);
    List<RecommendationEntity> modelListToEntityList(List<Recommendation> model);
}
