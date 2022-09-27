package com.kbe.productservice.domain.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbe.productservice.entity.HardwareRepository;
import com.kbe.productservice.entity.ProductRepository;
import com.kbe.productservice.config.RabbitConfig;
import com.kbe.productservice.entity.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class QueueListener {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private HardwareRepository hardwareRepository;
    @Autowired
    private ProductRepository productRepository;

    @RabbitListener(queues = RabbitConfig.GETHARDWAREQUEUE)
    public String OnGetHardwareRequest(Currency currency){
        System.out.println("GetHardwareRequest received");

        var hardwareListFromDB = hardwareRepository.findAll();
        List<Hardware> hardwareList = new ArrayList<>();
        for (Hardware hardware: hardwareListFromDB) {
            hardwareList.add(hardware);
        }
        for (Hardware hardware: hardwareList) {
            hardware.setPrice(getPriceInCurrency(hardware.getPrice(), currency));
        }

        String output = getJsonFromHardwareList(hardwareList);
        return output;
    }

    @RabbitListener(queues = RabbitConfig.GETPRODUCTSQUEUE)
    public String OnGetProductsRequest(Currency currency){
        System.out.println("OnGetProductsRequest received");

        var products = productRepository.findAll();
        Iterator iterator = products.iterator();
        List<Product> productList = new ArrayList<>();
        while(iterator.hasNext()){
            Product product = (Product) iterator.next();
            productList.add(product);
        }
        for (Product product: products) {
            product.setPrice(getPriceOfHardwareComponents(product, currency));
        }
        for (Product product: products) {
            product.setPrice(getPriceInCurrency(product.getPrice(), currency));
        }

        String output = getJsonFromProductList(productList);
        return output;
    }

    private String getJsonFromHardwareList(List<Hardware> hardwareList){
        String output = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            output = mapper.writeValueAsString(hardwareList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        output = "{\"hardwarelist\":" + output + "}";
        return output;
    }

    private String getJsonFromProductList(List<Product> productList){
        String output = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            output = mapper.writeValueAsString(productList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        output = "{\"productlist\":" + output + "}";
        return output;
    }

    private double getPriceOfHardwareComponents(Product product, Currency currency){
        List<Hardware> hardware = product.getHardware();
        double[] prices = new double[hardware.size()];
        for (int i = 0; i < hardware.size(); i++) {
            prices[i] = getPriceInCurrency(hardware.get(i).getPrice(), currency);
        }
        PriceRequestCall priceRequest = new PriceRequestCall(-1, prices);
        var value = rabbitTemplate.convertSendAndReceive(RabbitConfig.PRICEREQUESTEXCHANGE, RabbitConfig.PRICESERVICEROUTINGKEY, priceRequest);
        if(value == null) return -1;
        return (Double)value;
    }

    private double getPriceInCurrency(double price, Currency currency){
        CurrencyRequest currencyRequest = new CurrencyRequest(-1, price, currency);
        var value = rabbitTemplate.convertSendAndReceive(RabbitConfig.CURRENCYREQUESTEXCHANGE, RabbitConfig.CURRENCYSERVICEROUTINGKEY, currencyRequest);
        if(value == null) return -1;
        return (Double)value;
    }

    @RabbitListener(queues = RabbitConfig.CREATEPRODUCTQUEUE)
    public void OnCreateProductRequest(APICrudRequest requestCall){
        Product product = getProductFromDB(requestCall);
        productRepository.save(product);
    }

    @RabbitListener(queues = RabbitConfig.UPDATEPRODUCTQUEUE)
    public void OnUpdateProductRequest(APICrudRequest requestCall){
        Product product = getProductFromDB(requestCall);
        productRepository.deleteById(requestCall.getId());
        productRepository.save(product);
    }

    @RabbitListener(queues = RabbitConfig.DELETEPRODUCTQUEUE)
    public void OnDeleteProductRequest(APICrudRequest requestCall){
        productRepository.deleteById(requestCall.getId());
    }

    private Product getProductFromDB(APICrudRequest requestCall){
        List<Hardware> hardwareList = new ArrayList<>();
        for (int id :requestCall.getHardwareIds()) {
            Hardware hardware = hardwareRepository.findById(id).get();
            hardwareList.add(hardware);
        }

        Product product = new Product();
        product.setName(requestCall.getName());
        product.setHardware(hardwareList);
        return product;
    }
}
