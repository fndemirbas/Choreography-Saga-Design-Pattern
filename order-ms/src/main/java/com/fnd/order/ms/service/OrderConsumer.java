package com.fnd.order.ms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnd.order.ms.repository.OrderRepository;
import com.fnd.order.ms.dto.OrderEvent;
import com.fnd.order.ms.entity.OrderTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component //can be annotated with @Service as well
public class OrderConsumer {

    @Autowired
    private OrderRepository repository;

    @KafkaListener(topics = "payment-to-order", groupId = "orders-group")
    public void consume(String event){
        try {
            //read order event coming from payment ms
            OrderEvent orderEvent = new ObjectMapper().readValue(event,OrderEvent.class);

            //find order in db
            Optional<OrderTable> order = repository.findById(orderEvent.getOrder().getOrderId());
            order.ifPresent(o -> {
                if (orderEvent.getType().equalsIgnoreCase("ORDER_COMPLETED")) {
                    o.setStatus("ORDER_COMPLETED");
                } else {
                    o.setStatus("ORDER_CANCELLED");
                }
                repository.save(o);
            });

        }catch (Exception e){
            System.out.println("Exception in OrderConsumer::consume");
            e.printStackTrace();
        }
    }
}
