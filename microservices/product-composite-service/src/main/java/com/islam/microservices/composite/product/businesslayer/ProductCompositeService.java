package com.islam.microservices.composite.product.businesslayer;

import com.islam.api.composite.product.ProductAggregate;
import org.springframework.stereotype.Service;


public interface ProductCompositeService {

    public ProductAggregate getProduct(int productId);

    public void createProduct (ProductAggregate model);

    public void deleteProduct (int productId);
}
