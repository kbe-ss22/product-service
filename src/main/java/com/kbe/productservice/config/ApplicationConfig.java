package com.kbe.productservice.config;

import com.kbe.productservice.domain.converter.ListToJsonConverter;
import com.kbe.productservice.domain.converter.ListToJsonConverterImpl;
import com.kbe.productservice.domain.db.DBHandler;
import com.kbe.productservice.domain.db.DBHandlerImpl;
import com.kbe.productservice.domain.listener.QueueRequestHandler;
import com.kbe.productservice.domain.listener.QueueRequestHandlerImpl;
import com.kbe.productservice.domain.sender.RequestSender;
import com.kbe.productservice.domain.sender.RequestSenderImpl;
import org.springframework.context.annotation.Bean;

public class ApplicationConfig {

    @Bean
    public QueueRequestHandler queueRequestHandler(){ return new QueueRequestHandlerImpl(); }

    @Bean
    public ListToJsonConverter listToJsonConverter(){ return new ListToJsonConverterImpl(); }

    @Bean
    public DBHandler dbHandler(){ return new DBHandlerImpl(); }

    @Bean
    public RequestSender requestSender(){ return new RequestSenderImpl(); }
}
