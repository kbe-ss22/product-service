package com.kbe.productservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "product")
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private double price; // should only be set before sending it to api
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Hardware> hardware = new ArrayList<>();
}
