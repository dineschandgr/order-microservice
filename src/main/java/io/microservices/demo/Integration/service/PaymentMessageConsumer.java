package io.microservices.demo.Integration.service;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentMessageConsumer {


    @RabbitListener(queues = "payment-queue")
    public void receiveMessage(String message)
    {
        // Handle the received message here
        System.out.println("Received message from payment queue: " + message);
    }

    @RabbitListener(queues = "queue-name")
    public void receiveMessageQueueName(String message)
    {
        // Handle the received message here
        System.out.println("Received message from Queue Name queue: " + message);
    }
}