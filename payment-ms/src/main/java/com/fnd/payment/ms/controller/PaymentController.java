package com.fnd.payment.ms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnd.payment.ms.dto.CustomerOrder;
import com.fnd.payment.ms.dto.OrderEvent;
import com.fnd.payment.ms.dto.PaymentEvent;
import com.fnd.payment.ms.entity.Payment;
import com.fnd.payment.ms.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class PaymentController {

    @Autowired
    private PaymentRepository repository;

    @Autowired
    private KafkaTemplate<String, PaymentEvent> paymentKafkaTemplate;

    @Autowired
    private KafkaTemplate<String, OrderEvent> orderKafkaTemplate;

    @KafkaListener(topics = "new-orders", groupId = "orders-group")
    public void processPayment(String event) throws JsonProcessingException {
        System.out.println("processPayment event: " + event);

        OrderEvent orderEvent = new ObjectMapper().readValue(event,OrderEvent.class);
        CustomerOrder customerOrder = orderEvent.getOrder();

        Payment payment = new Payment();

        try { // if anything fails in payment, execute catch
            payment.setAmount(customerOrder.getAmount());
            payment.setMode(customerOrder.getPaymentMode());
            payment.setOrderId(customerOrder.getOrderId());
            payment.setStatus("PAYMENT_CREATED");
            repository.save(payment);

            PaymentEvent paymentEvent = new PaymentEvent();
            paymentEvent.setOrder(customerOrder);
            paymentEvent.setType("PAYMENT_CREATED");

            paymentKafkaTemplate.send("new-payments",paymentEvent);

            OrderEvent reverseOrderEvent = new OrderEvent();
            reverseOrderEvent.setOrder(customerOrder);
            reverseOrderEvent.setType("ORDER_COMPLETED");
            orderKafkaTemplate.send("payment-to-order",reverseOrderEvent);
        }catch (Exception e){
            payment.setStatus("PAYMENT_FAILED");
            repository.save(payment);

            OrderEvent reverseOrderEvent = new OrderEvent();
            reverseOrderEvent.setOrder(customerOrder);
            reverseOrderEvent.setType("ORDER_CANCELLED");
            orderKafkaTemplate.send("payment-to-order",reverseOrderEvent);
        }
    }
}
