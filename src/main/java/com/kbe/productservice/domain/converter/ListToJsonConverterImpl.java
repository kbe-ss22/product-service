package com.kbe.productservice.domain.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbe.productservice.entity.Hardware;
import com.kbe.productservice.entity.Product;

import java.util.List;

public class ListToJsonConverterImpl implements ListToJsonConverter{
    @Override
    public String getJsonFromHardwareList(List<Hardware> hardwareList) {
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

    @Override
    public String getJsonFromProductList(List<Product> productList) {
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
}
