package com.kbe.productservice.domain.listener;

import com.kbe.productservice.domain.converter.ListToJsonConverter;
import com.kbe.productservice.domain.db.DBHandler;
import com.kbe.productservice.domain.sender.RequestSender;
import com.kbe.productservice.entity.*;
import com.kbe.productservice.entity.Hardware;
import com.kbe.productservice.entity.Product;
import com.kbe.productservice.entity.services.APICrudRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class QueueRequestHandlerImpl implements QueueRequestHandler {


    @Autowired
    private ListToJsonConverter listToJsonConverter;
    @Autowired
    private DBHandler dbHandler;
    @Autowired
    private RequestSender requestSender;

    @Override
    public String getHardware(Currency currency) {

        List<Hardware> hardwareList = dbHandler.getHardwareFromDB();
        calculatePricesForAllHardwareOfList(hardwareList, currency);
        return listToJsonConverter.getJsonFromHardwareList(hardwareList);
    }

    @Override
    public String getProducts(Currency currency) {

        List<Product> products = dbHandler.getProductsFromDB();
        calculatePricesForProductsInCurrency(products, currency);
        return listToJsonConverter.getJsonFromProductList(products);
    }

    @Override
    public void createProduct(APICrudRequest requestCall) {
        dbHandler.createProductInDB(requestCall);
    }

    @Override
    public void updateProduct(APICrudRequest requestCall) {
        dbHandler.updateProductInDB(requestCall);
    }

    @Override
    public void deleteProduct(APICrudRequest requestCall) {
        dbHandler.deleteProductInDB(requestCall);
    }

    private void calculatePricesForAllHardwareOfList(List<Hardware> hardwareList, Currency currency){
        for (Hardware hardware: hardwareList) {
            hardware.setPrice(requestSender.getPriceInCurrency(hardware.getPrice(), currency));
        }
    }

    private void calculatePricesForProductsInCurrency(List<Product> products, Currency currency){
        for (Product product: products) {
            double tmpPrice = requestSender.getPriceOfProduct(product);
            product.setPrice(requestSender.getPriceInCurrency(tmpPrice, currency));
        }
    }
}
