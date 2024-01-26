package com.fnd.payment.ms.service;

import com.fnd.payment.ms.dto.CustomerOrder;
import com.fnd.payment.ms.dto.OrderEvent;
import com.fnd.payment.ms.dto.PaymentEvent;
import com.fnd.payment.ms.entity.Payment;
import com.fnd.payment.ms.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository repository;

    @Autowired
    private PaymentProducer paymentProducer;

    public void processPayment(OrderEvent orderEvent){
        CustomerOrder customerOrder = orderEvent.getOrder();
        Payment payment = new Payment();

        try {
            payment.setAmount(customerOrder.getAmount());
            payment.setMode(customerOrder.getPaymentMode());
            payment.setOrderId(customerOrder.getOrderId());
            payment.setStatus("PAYMENT_CREATED");
            repository.save(payment);

            paymentProducer.produce(customerOrder, "PAYMENT_CREATED");

        }catch (Exception e){
            payment.setStatus("PAYMENT_FAILED");
            repository.save(payment);
            paymentProducer.produceOrderEvent(customerOrder, "ORDER_CANCELLED");
        }
    }

    public void processStockEvent(PaymentEvent paymentEvent){
        CustomerOrder customerOrder = paymentEvent.getOrder();
        //find order in db
        Optional<Payment> payment = repository.findByOrderId(customerOrder.getOrderId());
        payment.ifPresent(p -> {
            if(paymentEvent.getType().equalsIgnoreCase("PAYMENT_REVERSED")){
                p.setStatus("PAYMENT_REVERSED");
                repository.save(p);
                paymentProducer.produceOrderEvent(customerOrder, "ORDER_CANCELLED");
            }
            //can add more else blocks
        });
    }
}
