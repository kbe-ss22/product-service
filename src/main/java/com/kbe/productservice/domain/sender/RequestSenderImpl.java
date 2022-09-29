package com.kbe.productservice.domain.sender;

import com.kbe.productservice.config.RabbitConfig;
import com.kbe.productservice.entity.*;
import com.kbe.productservice.entity.Hardware;
import com.kbe.productservice.entity.Product;
import com.kbe.productservice.entity.services.CurrencyRequest;
import com.kbe.productservice.entity.services.PriceRequestCall;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RequestSenderImpl implements RequestSender{
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public double getPriceOfProduct(Product product) {
        List<Hardware> hardware = product.getHardware();
        double[] prices = new double[hardware.size()];
        for (int i = 0; i < hardware.size(); i++) {
            prices[i] = hardware.get(i).getPrice();
        }
        PriceRequestCall priceRequest = new PriceRequestCall(-1, prices);
        var value = rabbitTemplate.convertSendAndReceive(RabbitConfig.PRICEREQUESTEXCHANGE, RabbitConfig.PRICESERVICEROUTINGKEY, priceRequest);
        if(value == null) return -1;
        return (Double)value;
    }

    @Override
    public double getPriceInCurrency(double price, Currency currency) {
        CurrencyRequest currencyRequest = new CurrencyRequest(-1, price, currency);
        var value = rabbitTemplate.convertSendAndReceive(RabbitConfig.CURRENCYREQUESTEXCHANGE, RabbitConfig.CURRENCYSERVICEROUTINGKEY, currencyRequest);
        if(value == null) return -1;
        return (Double)value;
    }
}
