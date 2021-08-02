package com.jonginout.orderservice.controller;

import com.jonginout.orderservice.dto.OrderDto;
import com.jonginout.orderservice.jpa.OrderEntity;
import com.jonginout.orderservice.service.OrderService;
import com.jonginout.orderservice.vo.RequestOrder;
import com.jonginout.orderservice.vo.ResponseOrder;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order-service")
public class OrderController {
    private Environment env;
    private OrderService orderService;

    @Autowired
    public OrderController(Environment env, OrderService orderService) {
        this.env = env;
        this.orderService = orderService;
    }

    @GetMapping("/health_check")
    public String healthCheck() {
        return "It's Working in Order Service.." + "port : " + env.getProperty("local.server.port");
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(
            @PathVariable String userId,
            @RequestBody RequestOrder request
    ) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = mapper.map(request, OrderDto.class);
        orderDto.setUserId(userId);
        OrderDto createdOrder = orderService.createOrder(orderDto);

        ResponseOrder responseOrder = mapper.map(createdOrder, ResponseOrder.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> createOrder(
            @PathVariable String userId
    ) {
        Iterable<OrderEntity> orderList = orderService.getOrdersByUserId(userId);

        List<ResponseOrder> result = new ArrayList<>();
        orderList.forEach(v -> {
            ResponseOrder responseOrder = new ModelMapper().map(v, ResponseOrder.class);
            result.add(responseOrder);
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
