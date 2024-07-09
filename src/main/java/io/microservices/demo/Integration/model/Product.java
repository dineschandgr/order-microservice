package io.microservices.demo.Integration.model;

import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Product {

    private  Integer id;

    private  String product_Name;
    private  String description;
    private  double price;
    private double gstPercentage;

    @Transient
    private Integer tempCategoryId;

    private User user;

}
