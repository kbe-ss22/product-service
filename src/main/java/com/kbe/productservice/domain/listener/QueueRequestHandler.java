package com.kbe.productservice.domain.listener;

import com.kbe.productservice.entity.services.APICrudRequest;
import com.kbe.productservice.entity.Currency;

public interface QueueRequestHandler {

    /**
     * Return all saved hardware from db in given currency
     * @param currency
     * @return json with the hardware data
     */
    String getHardware(Currency currency);

    /**
     * Return all saved products from db in given currency
     * @param currency
     * @return json with the product data
     */
    String getProducts(Currency currency);

    /**
     * Creates new product
     * @param requestCall everything has to be set except the id
     */
    void createProduct(APICrudRequest requestCall);

    /**
     * Deletes product
     * @param requestCall only id must be set
     */
    void deleteProduct(APICrudRequest requestCall);

    /**
     * Updates product
     * @param requestCall everything has to be set
     */
    void updateProduct(APICrudRequest requestCall);
}
