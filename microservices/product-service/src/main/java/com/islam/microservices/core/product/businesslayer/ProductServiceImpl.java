package com.islam.microservices.core.product.businesslayer;

import com.islam.api.core.product.Product;
import com.islam.microservices.core.product.datalayer.ProductEntity;
import com.islam.microservices.core.product.datalayer.ProductRepository;
import com.islam.utils.exceptions.InvalidInputException;
import com.islam.utils.exceptions.NotFoundException;
import com.islam.utils.http.ServiceUtils;
import com.mongodb.DuplicateKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService{

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository repository;

    private final ProductMapper mapper;

    private ServiceUtils serviceUtils;

    public ProductServiceImpl(ProductRepository repository, ProductMapper mapper, ServiceUtils serviceUtils) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtils = serviceUtils;
    }

    @Override
    public Product getProductById(int productId) {
        ProductEntity entity = repository.findByProductId(productId).orElseThrow(()-> new NotFoundException("No product found for productId: " + productId));
        Product model = mapper.entityToModel(entity);
        model.setServiceAddress(serviceUtils.getServiceAddress());
        LOG.debug("Product getProductById: found productId: {}", model.getProductId());
        return model;
    }

    @Override
    public Product createProduct(Product model) {
        try{
            ProductEntity entity = mapper.modelToEntity(model);
            ProductEntity newEntity = repository.save(entity);

            LOG.debug("createProduct: entity created for productId: {}", model.getProductId());

            return mapper.entityToModel(newEntity);
        }
        catch (DuplicateKeyException dke){
            throw new InvalidInputException("Duplicate key for productId: " + model.getProductId());
        }
    }

    @Override
    public void deleteProduct(int productId) {

        LOG.debug("deleteProduct: trying to delete entity with productId: {}", productId);
        repository.findByProductId(productId).ifPresent(e->repository.delete(e));
    }
}
