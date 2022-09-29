package com.kbe.productservice.entity.services;

import com.kbe.productservice.entity.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CurrencyRequest {

    private long id;
    private double price;
    private Currency currency;

}
