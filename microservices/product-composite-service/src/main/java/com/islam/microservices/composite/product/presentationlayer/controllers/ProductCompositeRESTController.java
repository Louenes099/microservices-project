package com.islam.microservices.composite.product.presentationlayer.controllers;

import com.islam.api.composite.product.ProductAggregate;
import com.islam.api.composite.product.ProductCompositeServiceAPI;
import com.islam.microservices.composite.product.businesslayer.ProductCompositeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductCompositeRESTController implements ProductCompositeServiceAPI {

    private static  final Logger log = LoggerFactory.getLogger(ProductCompositeRESTController.class);
    private final ProductCompositeService productCompositeService;

    public ProductCompositeRESTController(ProductCompositeService productCompositeService) {
        this.productCompositeService = productCompositeService;
    }


    @Override
    public ProductAggregate getCompositeProduct(int productId) {
        log.debug("ProductComposite REST received getCompositeProduct request for productId: {}", productId);
        ProductAggregate productAggregate = productCompositeService.getProduct(productId);
        return productAggregate;
    }

    @Override
    public void createCompositeProduct(ProductAggregate model) {
        log.debug("ProductComposite REST received createCompositeProduct request");
        productCompositeService.createProduct(model);
    }

    @Override
    public void deleteCompositeProduct(int productId) {
        log.debug("ProductComposite REST received getCompositeProduct request for productId: {}", productId);
        productCompositeService.deleteProduct(productId);
    }
}
