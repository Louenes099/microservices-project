package com.islam.microservices.core.product.businesslayer;

import com.islam.api.core.product.Product;
import com.islam.microservices.core.product.datalayer.ProductEntity;

public interface ProductService {

    public Product getProductById(int productId);

    public Product createProduct(Product model);

    public void deleteProduct(int productId);


}
