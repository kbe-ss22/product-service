package com.kbe.productservice.domain.converter;

import com.kbe.productservice.entity.Hardware;
import com.kbe.productservice.entity.Product;

import java.util.List;

public interface ListToJsonConverter {
    String getJsonFromHardwareList(List<Hardware> hardwareList);
    String getJsonFromProductList(List<Product> productList);
}
