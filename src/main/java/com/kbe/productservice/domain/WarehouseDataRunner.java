package com.kbe.productservice.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbe.productservice.entity.HardwareRepository;
import com.kbe.productservice.entity.ProductRepository;
import com.kbe.productservice.entity.Hardware;
import com.kbe.productservice.entity.Product;
import com.kbe.productservice.entity.WarehouseRequestData;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;


@Component
public class WarehouseDataRunner implements CommandLineRunner {

    private HardwareRepository hardwareRepository;
    private ProductRepository productRepository;



    // https://reflectoring.io/spring-webclient/
    // Todo check and change for right adress
    public String getDataFromWarehouse(){

        WebClient client = WebClient.create();
        WebClient.ResponseSpec responseSpec = client.get()
                .uri("http://localhost:8082/warehouse")
                .retrieve();
        String responseBody = responseSpec.bodyToMono(String.class).block();
        //System.out.println("responseBody: "+responseBody);
        return responseBody;

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

    public WarehouseDataRunner(HardwareRepository hardwareRepository, ProductRepository productRepository) {
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
