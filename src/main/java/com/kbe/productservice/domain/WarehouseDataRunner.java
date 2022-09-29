package com.kbe.productservice.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbe.productservice.domain.db.DBHandler;
import com.kbe.productservice.entity.warehouse.WarehouseRequestData;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;


@Component
public class WarehouseDataRunner implements CommandLineRunner {

    private DBHandler dbHandler;

    public WarehouseDataRunner(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    @Override
    public void run(String... args){
        getDataFromExternalSource();
    }

    private void getDataFromExternalSource(){
        String data = getDataFromWarehouse();
        if(data == null || data.equals("")) return;
        WarehouseRequestData dataObject = readJsonWithMapper(data);
        writeDataToDB(dataObject);
    }

    public String getDataFromWarehouse(){
        WebClient client = WebClient.create();
        WebClient.ResponseSpec responseSpec = client.get()
                .uri("http://"+"localhost"+":8010/warehouse") // if used local
                //.uri("http://"+"host.docker.internal"+":8080/warehouse") // if used in container
                .retrieve();
        String responseBody = responseSpec.bodyToMono(String.class).block();
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
        dbHandler.saveDataToDB(dataObject.getHardwareList(), dataObject.getProductList());
    }
}
