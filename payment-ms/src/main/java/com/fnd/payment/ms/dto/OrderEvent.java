package com.fnd.payment.ms.dto;

import lombok.Data;

//for kafka
@Data
public class OrderEvent {

    private CustomerOrder order;
    private String type;
}
