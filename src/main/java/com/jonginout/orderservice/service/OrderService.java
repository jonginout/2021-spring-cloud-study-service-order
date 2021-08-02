package com.jonginout.orderservice.service;

import com.jonginout.orderservice.dto.OrderDto;
import com.jonginout.orderservice.jpa.OrderEntity;

public interface OrderService {
    OrderDto createOrder(OrderDto orderDto);

    OrderDto getOrderByOrderId(String orderId);

    Iterable<OrderEntity> getOrdersByUserId(String userId);
}
