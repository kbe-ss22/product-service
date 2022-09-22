package com.kbe.productservice.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbe.productservice.entity.HardwareRepository;
import com.kbe.productservice.entity.ProductRepository;
import com.kbe.productservice.entity.Hardware;
import com.kbe.productservice.entity.Product;
import com.kbe.productservice.entity.WarehouseRequestData;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


@Component
public class WarehouseDataRunner implements CommandLineRunner {
    private final RestTemplate restTemplate;
    private HardwareRepository hardwareRepository;
    private ProductRepository productRepository;


    // Todo check and change for right adress
    public String getDataFromWarehouse(){
        String response = "";
        //String url = "http://localhost:8082/irgendwasGetHardwareOderSo";
        //response = this.restTemplate.getForObject(url, String.class);
        return response;
    }

    public WarehouseRequestData readJsonWithMapper(String json){
        ObjectMapper mapper = new ObjectMapper();
        WarehouseRequestData data = null;
        try {
            data = mapper.readValue(json, WarehouseRequestData.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private void writeDataToDB(WarehouseRequestData dataObject){
        for (Hardware hardware: dataObject.getHardwareList()) {
            hardwareRepository.save(hardware);
        }

        for (Product product: dataObject.getProductList()) {
            productRepository.save(product);
        }
    }

    public WarehouseDataRunner(RestTemplateBuilder builder, HardwareRepository hardwareRepository, ProductRepository productRepository) {
        this.restTemplate = builder.build();
        this.hardwareRepository = hardwareRepository;
        this.productRepository = productRepository;
    }


    @Override
    public void run(String... args) throws Exception {
        String data = getDataFromWarehouse();
        if(data == null || data.equals("")) return;
        WarehouseRequestData dataObject = readJsonWithMapper(data);
        writeDataToDB(dataObject);
    }
}
