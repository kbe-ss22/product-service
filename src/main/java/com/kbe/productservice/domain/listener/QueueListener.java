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
        //System.out.println("GetHardwareRequest received");
        //hardware aus db holen
        var hardwareList = hardwareRepository.findAll();
        List<Hardware> hList = new ArrayList<>();
        for (Hardware piece: hardwareList) {
            hList.add(piece);
            //System.out.println("hardware: " + piece.toString());
        }
        //System.out.println("hardware loaded from db: " + hList.size());
        //currency service nach preis fragen
        for (Hardware piece: hList) {
            //System.out.println("name of hardware: " + piece.getName() + ", price of hardware: " + piece.getPrice());
            CurrencyRequest currencyRequest = new CurrencyRequest(-1, piece.getPrice(), currency);
            var value = rabbitTemplate.convertSendAndReceive(RabbitConfig.CURRENCYREQUESTEXCHANGE, RabbitConfig.CURRENCYSERVICEROUTINGKEY, currencyRequest);
            piece.setPrice((Double) value);
        }
        //als json zurück geben
        String output = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            output = mapper.writeValueAsString(hList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //System.out.println("price calculation answer call with id: " + requestCall.getId() + " received, with sum: " + requestCall.getPrice());
        output = "{hardwarelist:" + output + "}";
        return output;
    }

    @RabbitListener(queues = RabbitConfig.GETPRODUCTSQUEUE)
    public String OnGetProductsRequest(Currency currency){
        var products = productRepository.findAll();
        Iterator iterator = products.iterator();
        List<Product> pList = new ArrayList<>();
        while(iterator.hasNext()){
            Product product = (Product) iterator.next();
            pList.add(product);
            //System.out.println("product: " + product);
        }
        /*
        for (Product piece: products) {

            pList.add(piece);
        }

         */
        //sum of hardware calculation
        for (Product product: products) {
            List<Hardware> hardware = product.getHardware();
            //System.out.println("product name: " + product.getName());
            //System.out.println("product first hardware: " + hardware.get(0).getName());
            double[] prices = new double[hardware.size()];
            for (int i = 0; i < hardware.size(); i++) {
                prices[i] = hardware.get(i).getPrice();
                CurrencyRequest currencyRequest = new CurrencyRequest(-1, prices[i], currency);
                var value = rabbitTemplate.convertSendAndReceive(RabbitConfig.CURRENCYREQUESTEXCHANGE, RabbitConfig.CURRENCYSERVICEROUTINGKEY, currencyRequest);
                prices[i] = (double) value;
            }
            PriceRequestCall priceRequest = new PriceRequestCall(-1, prices);
            var value = rabbitTemplate.convertSendAndReceive(RabbitConfig.PRICEREQUESTEXCHANGE, RabbitConfig.PRICESERVICEROUTINGKEY, priceRequest);
            product.setPrice((Double) value);
        }

        //currency service nach preis fragen
        for (Product piece: products) {
            CurrencyRequest currencyRequest = new CurrencyRequest(-1, piece.getPrice(), currency);
            var value = rabbitTemplate.convertSendAndReceive(RabbitConfig.CURRENCYREQUESTEXCHANGE, RabbitConfig.CURRENCYSERVICEROUTINGKEY, currencyRequest);

            piece.setPrice((Double) value);
        }
        //als json zurück geben
        String output = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            output = mapper.writeValueAsString(pList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //System.out.println("output: " + output);
        //System.out.println("price calculation answer call with id: " + requestCall.getId() + " received, with sum: " + requestCall.getPrice());
        output = "{productlist:" + output + "}";
        return output;
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
        //product erzeugen
        Product product = new Product();
        product.setName(requestCall.getName());
        product.setHardware(hardwareList);
        return product;
    }
}
