package com.kbe.productservice.entity.services;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PriceRequestCall {

    private long id;
    private double[] prices;
}
