package com.kbe.productservice.domain.db;

import com.kbe.productservice.entity.services.APICrudRequest;
import com.kbe.productservice.entity.Hardware;
import com.kbe.productservice.entity.Product;

import java.util.List;

public interface DBHandler {

    List<Hardware> getHardwareFromDB();

    List<Product> getProductsFromDB();

    /**
     * Creates new product in db
     * @param requestCall everything has to be set except the id
     */
    void createProductInDB(APICrudRequest requestCall);

    /**
     * Deletes product from db
     * @param requestCall only id must be set
     */
    void updateProductInDB(APICrudRequest requestCall);

    /**
     * Updates product in db
     * @param requestCall everything has to be set
     */
    void deleteProductInDB(APICrudRequest requestCall);

    void saveDataToDB(List<Hardware> hardwareList, List<Product> products);
}
