package com.kbe.productservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    //API Gateway
    public static final String GETHARDWAREQUEUE = "get-hardware-queue";
    public static final String GETPRODUCTSQUEUE = "get-products-queue";
    public static final String CREATEPRODUCTQUEUE = "create-product-queue";
    public static final String UPDATEPRODUCTQUEUE = "update-product-queue";
    public static final String DELETEPRODUCTQUEUE = "delete-product-queue";

    //Price Service
    public static final String PRICEREQUESTEXCHANGE = "priceservice-request-exchange";
    public static final String PRICEREQUESTQUEUE = "priceservice-request-queue";
    public static final String PRICESERVICEROUTINGKEY = "priceservice";

    //CurrencyService
    public static final String CURRENCYREQUESTEXCHANGE = "currency-request-exchange";
    public static final String CURRENCYREQUESTQUEUE = "currency-request-queue";
    public static final String CURRENCYSERVICEROUTINGKEY = "currency";

    @Bean
    public Queue getHardwareQueue() {
        return new Queue(GETHARDWAREQUEUE, false);
    }
    @Bean
    public Queue getProductsQueue() {
        return new Queue(GETPRODUCTSQUEUE, false);
    }
    @Bean
    public Queue createProductQueue() {
        return new Queue(CREATEPRODUCTQUEUE, false);
    }
    @Bean
    public Queue updateProductQueue() {
        return new Queue(UPDATEPRODUCTQUEUE, false);
    }
    @Bean
    public Queue deleteProductQueue() {
        return new Queue(DELETEPRODUCTQUEUE, false);
    }


    @Bean
    public Queue priceServiceQueue() {
        return new Queue(PRICEREQUESTQUEUE, false);
    }

    @Bean
    public Queue currencyServiceQueue() {
        return new Queue(CURRENCYREQUESTQUEUE, false);
    }

    @Bean
    public TopicExchange priceServiceExchange() {
        return new TopicExchange(PRICEREQUESTEXCHANGE);
    }

    @Bean
    public TopicExchange currencyServiceExchange() {
        return new TopicExchange(CURRENCYREQUESTEXCHANGE);
    }

    @Bean
    public Binding priceServiceExchangeBinding() {
        return BindingBuilder.bind(priceServiceQueue()).to(priceServiceExchange()).with(PRICESERVICEROUTINGKEY);
    }

    @Bean
    public Binding currencyServiceExchangeBinding() {
        return BindingBuilder.bind(currencyServiceQueue()).to(currencyServiceExchange()).with(CURRENCYSERVICEROUTINGKEY);
    }

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory){
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
