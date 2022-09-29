# product-service
Microservice which manages hardware and product data. Should be used in an docker container and communicates with rabbitmq. 
After the initialisation it requests hardware and product data from warehouse and saves the data in its own db.
It uses currency service and price service. All requests to the product service will come from the gateway.

Currently it cant be run in docker container. It runs only local. 
Conditions bevore the start:
:rabbit server should running
:warehouse should running
:db should running -> currently it connects itself to the same postgres instance where the warehouse is. you have to create manualy the product db bevor starting this service
