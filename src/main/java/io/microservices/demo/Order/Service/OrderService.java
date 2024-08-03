package io.microservices.demo.Order.Service;

import io.microservices.demo.Cart.Repository.CartRepository;
import io.microservices.demo.Cart.Service.CartService;
import io.microservices.demo.Cart.model.Cart;
import io.microservices.demo.Cart.model.CartItem;
import io.microservices.demo.Order.Controller.OrderController;
import io.microservices.demo.Order.Repository.OrderItemRepository;
import io.microservices.demo.Order.Repository.OrderRepository;
import io.microservices.demo.Order.model.EOrderStatus;
import io.microservices.demo.Order.model.Order;
import io.microservices.demo.Order.model.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    CartRepository cartRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    public Order createOrder(Cart cart) {
        Order order = Order.builder().orderStatus(EOrderStatus.PENDING).orderTotal(cart.getTotalPrice())
                .gstAmount(cart.getGstAmount())
                .totalAmountWithGST(cart.getTotalAmountWithGST()).userId(cart.getUserId()).build();

        orderRepository.save(order);

        //  Map Cart Items to Order Items
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem items : cart.getCartItem()) {
            OrderItem orderItem = OrderItem.builder().order(order).itemTotal(items.getItemTotal()).productId(items.getProductId())
                    .quantity(items.getQuantity()).gstAmount(items.getGstAmount())
                    .productPrice(items.getProductPrice())
                    .build();

            orderItems.add(orderItem);
        }

        //  Save Order Items using order service
        orderItemRepository.saveAll(orderItems);
        return order;

    }

    public void saveOrderItems(List<OrderItem> orderItems) {
        orderItemRepository.saveAll(orderItems);

    }

    public Order findOrder(Long userId, Integer orderId) throws Exception {
        Optional<Order> maybeOrder = orderRepository.findByUserIdAndId(userId, orderId);

        if(maybeOrder.isPresent()){
            return maybeOrder.get();
        }else{
            throw new Exception("Order not found");
        }

    }

    public Order getOrderById(Integer id) throws Exception {
        Optional<Order> order = orderRepository.findById(id);
        return order.orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public void completeOrder(Integer orderId) throws Exception {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        LOGGER.info("inside completeOrder {} ",order);
        order.setOrderStatus(EOrderStatus.COMPLETED);
        orderRepository.save(order);
        cartRepository.deleteByUserId(order.getUserId());
        //cartService.deleteCart(order.getUserId());

        //delete cart

    }

    public void cancelOrder(Integer orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        LOGGER.info("inside completeOrder {} ",order);
        order.setOrderStatus(EOrderStatus.CANCELLED);
        orderRepository.save(order);
    }
}
