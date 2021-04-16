package com.albayrak.microservices.core.product.presentationlayer.controllers;

import com.albayrak.api.core.product.Product;
import com.albayrak.api.core.product.ProductServiceAPI;
import com.albayrak.utils.exceptions.InvalidInputException;
import com.albayrak.utils.exceptions.NotFoundException;
import com.albayrak.utils.http.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductRESTController implements ProductServiceAPI {


    private static final Logger LOG = LoggerFactory.getLogger(ProductRESTController.class);

    private final ServiceUtils serviceUtils;
    public ProductRESTController(ServiceUtils serviceUtils){
        this.serviceUtils = serviceUtils;
    }


    @Override
    public Product getProduct(int productId) {
        LOG.debug("/product MS return the found product for productId: " + productId);

        if(productId < 1) throw new InvalidInputException("Invalid productId: " + productId);
        if (productId == 13) throw new NotFoundException("No product found for productId: " + productId);

        return new Product(productId, "name-" + productId, 123, serviceUtils.getServiceAddress());
    }
}
