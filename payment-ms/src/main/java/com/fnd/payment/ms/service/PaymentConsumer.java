package com.fnd.payment.ms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnd.payment.ms.dto.OrderEvent;
import com.fnd.payment.ms.dto.PaymentEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component //can be annotated with @Service as well
public class PaymentConsumer {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private KafkaTemplate<String, OrderEvent> orderKafkaTemplate;

    @KafkaListener(topics = "new-orders", groupId = "orders-group")
    public void consumeOrderAndProcessPayment(String event) throws JsonProcessingException {
        OrderEvent orderEvent = new ObjectMapper().readValue(event, OrderEvent.class);
        paymentService.processPayment(orderEvent);
    }

    @KafkaListener(topics = "reversed-payments", groupId = "payments-group")
    public void consumeFromStock(String event) throws JsonProcessingException {
        PaymentEvent paymentEvent = new ObjectMapper().readValue(event, PaymentEvent.class);
        paymentService.processStockEvent(paymentEvent);
    }
}
