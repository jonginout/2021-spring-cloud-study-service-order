package com.jonginout.orderservice.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ResponseOrder implements Serializable {
    private String productId;
    private Integer qty;
    private Integer unitPrice;
    private Integer totalPrice;

    private Date createdAt;

    private String orderId;
}
