package io.microservices.demo.Integration.service;

import io.microservices.demo.Integration.model.Product;
import io.microservices.demo.Integration.model.User;
import org.springframework.stereotype.Service;

@Service
public class CommonService {


    public User findUserById(Long userId) {
        return new User();
    }

    public Product findProductById(Integer userId) {
        return new Product();
    }
}
