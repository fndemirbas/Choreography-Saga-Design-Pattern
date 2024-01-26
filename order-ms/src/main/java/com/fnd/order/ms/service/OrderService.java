package com.fnd.order.ms.service;

import com.fnd.order.ms.repository.OrderRepository;
import com.fnd.order.ms.dto.CustomerOrder;
import com.fnd.order.ms.entity.OrderTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProducer orderProducer;

    public CustomerOrder createOrder(CustomerOrder customerOrder) {
        //fill entity
        OrderTable order = new OrderTable();
        order.setItem(customerOrder.getItem());
        order.setQuantity(customerOrder.getQuantity());
        order.setAmount(customerOrder.getAmount());
        order.setStatus("ORDER_CREATED");

        //save entity
        order = orderRepository.save(order);
        //set id in response
        customerOrder.setOrderId(order.getId());

        // call producer
        orderProducer.produce(customerOrder);

        return customerOrder;
    }
}
