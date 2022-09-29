package com.kbe.productservice.domain.sender;

import com.kbe.productservice.entity.Currency;
import com.kbe.productservice.entity.Product;

public interface RequestSender {
    double getPriceOfProduct(Product product);
    double getPriceInCurrency(double price, Currency currency);
}
