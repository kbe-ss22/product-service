package com.kbe.productservice.domain.db;

import com.kbe.productservice.entity.Hardware;
import com.kbe.productservice.entity.db.HardwareRepository;
import com.kbe.productservice.entity.Product;
import com.kbe.productservice.entity.db.ProductRepository;
import com.kbe.productservice.entity.services.APICrudRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class DBHandlerImpl implements DBHandler{

    @Autowired
    private HardwareRepository hardwareRepository;
    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Hardware> getHardwareFromDB() {
        var hardwareListFromDB = hardwareRepository.findAll();
        List<Hardware> hardwareList = new ArrayList<>();
        for (Hardware hardware: hardwareListFromDB) {
            hardwareList.add(hardware);
        }
        return hardwareList;
    }

    @Override
    public List<Product> getProductsFromDB() {
        var products = productRepository.findAll();
        Iterator iterator = products.iterator();
        List<Product> productList = new ArrayList<>();
        while(iterator.hasNext()){
            Product product = (Product) iterator.next();
            productList.add(product);
        }
        return productList;
    }

    @Override
    public void createProductInDB(APICrudRequest requestCall) {
        Product product = buildProductFromRequest(requestCall);
        productRepository.save(product);
    }

    @Override
    public void updateProductInDB(APICrudRequest requestCall) {
        Product product = buildProductFromRequest(requestCall);
        productRepository.deleteById(requestCall.getId());
        productRepository.save(product);
    }

    @Override
    public void deleteProductInDB(APICrudRequest requestCall) {
        productRepository.deleteById(requestCall.getId());
    }

    @Override
    public void saveDataToDB(List<Hardware> hardwareList, List<Product> products) {
        for (Hardware hardware: hardwareList) {
            hardwareRepository.save(hardware);
        }

        for (Product product: products) {
            productRepository.save(product);
        }
    }

    private Product buildProductFromRequest(APICrudRequest requestCall){
        List<Hardware> hardwareList = getHardwareListFromHardwareIds(requestCall.getHardwareIds());
        Product product = new Product();
        product.setName(requestCall.getName());
        product.setHardware(hardwareList);
        product.setPrice(0);
        return product;
    }

    private List<Hardware> getHardwareListFromHardwareIds(int[] ids){
        List<Hardware> hardwareList = new ArrayList<>();
        for (int id :ids) {
            Optional<Hardware> optionalHardware = hardwareRepository.findById(id);
            if(optionalHardware.isEmpty()) {
                continue;
            }
            Hardware hardware = optionalHardware.get();
            hardwareList.add(hardware);
        }
        return hardwareList;
    }
}
