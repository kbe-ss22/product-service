package com.kbe.productservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class APICrudRequest {
    private int id;
    private String name;
    private int[] hardwareIds = new int[10];
}
