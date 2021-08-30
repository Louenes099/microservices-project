package com.islam.microservices.core.product.presentationlayer.controllers;

import com.islam.api.core.product.Product;
import com.islam.api.core.product.ProductServiceAPI;
import com.islam.microservices.core.product.businesslayer.ProductService;
import com.islam.utils.exceptions.InvalidInputException;
import com.islam.utils.http.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductRESTController implements ProductServiceAPI {

    private static final Logger LOG = LoggerFactory.getLogger(ProductRESTController.class);
    private final ProductService productService;
    public ProductRESTController(ProductService productService){
        this.productService = productService;
    }


    @Override
    public Product getProduct(int productId) {
        LOG.debug("/product MS return the found product for productId: " + productId);

        if(productId < 1) throw new InvalidInputException("Invalid productId: " + productId);
        //if (productId == 13) throw new NotFoundException("No product found for productId: " + productId);

        Product product = productService.getProductById(productId);
        return product;
    }

    @Override
    public Product createProduct(Product model) {
        Product product = productService.createProduct(model);
        LOG.debug("REST createProduct: product created for productId: {}", product.getProductId());
        return product;
    }

    @Override
    public void deleteProduct(int productId) {

        LOG.debug("REST deleteProduct: tried to delete productId: {}", productId);
        productService.deleteProduct(productId);
    }
}
