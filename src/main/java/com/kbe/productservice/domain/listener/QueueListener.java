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
import java.util.Optional;

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
        int hardwarecount = requestCall.getHardwareIds().length;
        if(hardwarecount < 8 || hardwarecount > 9) return;
        System.out.println(requestCall.getName() + " hardware component count: " + requestCall.getHardwareIds().length);
        Product product = buildProductFromRequest(requestCall);

        System.out.println("product to save: " + product.getName() + " product price: " + product.getPrice());
        System.out.println("id: " + product.getId() + " product hardware: " + product.getHardware().size());
        printHardwareList(product.getHardware());
        productRepository.save(product);
    }

    @RabbitListener(queues = RabbitConfig.UPDATEPRODUCTQUEUE)
    public void OnUpdateProductRequest(APICrudRequest requestCall){
        Product product = buildProductFromRequest(requestCall);
        productRepository.deleteById(requestCall.getId());
        productRepository.save(product);
    }

    @RabbitListener(queues = RabbitConfig.DELETEPRODUCTQUEUE)
    public void OnDeleteProductRequest(APICrudRequest requestCall){
        productRepository.deleteById(requestCall.getId());
    }

    private Product buildProductFromRequest(APICrudRequest requestCall){
        List<Hardware> hardwareList = new ArrayList<>();
        for (int id :requestCall.getHardwareIds()) {
            Optional<Hardware> optionalHardware = hardwareRepository.findById(id);
            if(optionalHardware.isEmpty()) {
                continue;
            }
            Hardware hardware = optionalHardware.get();
            hardwareList.add(hardware);
        }
        Product product = new Product();
        product.setName(requestCall.getName());
        product.setHardware(hardwareList);
        product.setPrice(0);
        return product;
    }

    public static void printHardwareList(List<Hardware> hardwareList){
        for (Hardware hardware: hardwareList) {
            printHardware(hardware);
        }
    }

    public static void printHardware(Hardware hardware){
        System.out.println("id: " + hardware.getId() + ", name: " + hardware.getName() +
                ", price: " + hardware.getPrice() + ", type: " + hardware.getType() + ", description: " + hardware.getDescription() +
                ", stock: " + hardware.getStock());

    }
}
