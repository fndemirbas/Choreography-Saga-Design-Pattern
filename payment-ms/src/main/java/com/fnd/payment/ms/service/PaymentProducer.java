package com.fnd.payment.ms.service;

import com.fnd.payment.ms.dto.CustomerOrder;
import com.fnd.payment.ms.dto.OrderEvent;
import com.fnd.payment.ms.dto.PaymentEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentProducer {

    @Autowired
    private KafkaTemplate<String, PaymentEvent> paymentKafkaTemplate;

    @Autowired
    private KafkaTemplate<String, OrderEvent> orderKafkaTemplate;

    public void produce(CustomerOrder customerOrder, String type){
        PaymentEvent paymentEvent = new PaymentEvent();
        paymentEvent.setOrder(customerOrder);
        paymentEvent.setType(type);
        paymentKafkaTemplate.send("new-payments",paymentEvent);
    }

    public void produceOrderEvent(CustomerOrder customerOrder, String type){
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrder(customerOrder);
        orderEvent.setType(type);
        orderKafkaTemplate.send("payment-to-order",orderEvent);
    }
}
