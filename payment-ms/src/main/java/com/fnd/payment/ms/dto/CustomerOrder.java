package com.fnd.payment.ms.dto;


import lombok.Data;

@Data
public class CustomerOrder {

    private String item;

    private int quantity;

    private double amount;

    private String paymentMode;

    private long orderId;

    private String address;

}
