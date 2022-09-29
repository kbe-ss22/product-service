package com.kbe.productservice.domain.listener;

import com.kbe.productservice.config.ApplicationConfig;
import com.kbe.productservice.entity.*;
import com.kbe.productservice.entity.services.APICrudRequest;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class QueueListener {

    @Autowired
    private QueueRequestHandler queueListenerUtils;


    @RabbitListener(queues = ApplicationConfig.GETHARDWAREQUEUE)
    public String onGetHardwareRequest(Currency currency){
        return queueListenerUtils.getHardware(currency);
    }

    @RabbitListener(queues = ApplicationConfig.GETPRODUCTSQUEUE)
    public String onGetProductsRequest(Currency currency){
        return queueListenerUtils.getProducts(currency);
    }

    @RabbitListener(queues = ApplicationConfig.CREATEPRODUCTQUEUE)
    public void onCreateProductRequest(APICrudRequest requestCall){
        queueListenerUtils.createProduct(requestCall);
    }

    @RabbitListener(queues = ApplicationConfig.UPDATEPRODUCTQUEUE)
    public void onUpdateProductRequest(APICrudRequest requestCall){
        queueListenerUtils.updateProduct(requestCall);
    }

    @RabbitListener(queues = ApplicationConfig.DELETEPRODUCTQUEUE)
    public void onDeleteProductRequest(APICrudRequest requestCall){
        queueListenerUtils.deleteProduct(requestCall);
    }
}
