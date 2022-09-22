package com.kbe.productservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WarehouseRequestData {
    private List<Hardware> hardwareList = new ArrayList<>();
    private List<Product> productList = new ArrayList<>();
}
