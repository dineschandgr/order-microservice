package io.microservices.demo.Order.Controller;

import io.microservices.demo.Cart.Service.CartService;
import io.microservices.demo.Cart.model.Cart;
import io.microservices.demo.Cart.model.CartDTO;
import io.microservices.demo.Cart.model.CartItem;
import io.microservices.demo.Configuration.UserContext;
import io.microservices.demo.Order.Service.OrderService;
import io.microservices.demo.Order.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    private final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Integer id) throws Exception {
        LOGGER.info("Inside getOrder {} ",id);
        return orderService.getOrderById(id);
    }

}
