package io.microservices.demo.Integration.service;


import io.microservices.demo.Order.Service.OrderService;
import io.microservices.demo.Payment.model.PaymentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentMessageConsumer {

    @Autowired
    OrderService orderService;

    private final Logger LOGGER = LoggerFactory.getLogger(PaymentMessageConsumer.class);

    @RabbitListener(queues = "payment-queue")
    public void receiveMessage(PaymentDTO paymentDTO) throws Exception {
        // Handle the received message here
        LOGGER.info("Received message from payment queue: {} ", paymentDTO);

        if(paymentDTO.getPaymentMessage().equalsIgnoreCase("success"))
            orderService.completeOrder(paymentDTO.getOrderId());
        else
            orderService.cancelOrder(paymentDTO.getOrderId());
    }

}