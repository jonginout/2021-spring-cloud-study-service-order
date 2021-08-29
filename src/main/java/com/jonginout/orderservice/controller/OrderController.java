package com.jonginout.orderservice.controller;

import com.jonginout.orderservice.dto.OrderDto;
import com.jonginout.orderservice.jpa.OrderEntity;
import com.jonginout.orderservice.messagequeue.KafkaProducer;
import com.jonginout.orderservice.service.OrderService;
import com.jonginout.orderservice.vo.RequestOrder;
import com.jonginout.orderservice.vo.ResponseOrder;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order-service")
public class OrderController {
    private Environment env;
    private OrderService orderService;
    private KafkaProducer kafkaProducer;

    @Autowired
    public OrderController(Environment env, OrderService orderService, KafkaProducer kafkaProducer) {
        this.env = env;
        this.orderService = orderService;
        this.kafkaProducer = kafkaProducer;
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
        log.info("::::::::::::::::::::::::::: Before added orders data");

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = mapper.map(request, OrderDto.class);
        orderDto.setUserId(userId);
        OrderDto createdOrder = orderService.createOrder(orderDto);

        ResponseOrder responseOrder = mapper.map(createdOrder, ResponseOrder.class);

        // producer
        kafkaProducer.send("test-topic", orderDto);

        log.info("::::::::::::::::::::::::::: After added orders data");

        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> createOrder(
            @PathVariable String userId
    ) throws Exception {
        log.info("::::::::::::::::::::::::::: Before retrieve orders data");

        Iterable<OrderEntity> orderList = orderService.getOrdersByUserId(userId);

        List<ResponseOrder> result = new ArrayList<>();
        orderList.forEach(v -> {
            ResponseOrder responseOrder = new ModelMapper().map(v, ResponseOrder.class);
            result.add(responseOrder);
        });

        try {
            Thread.sleep(1000);
            throw new Exception("장애발생");
        } catch (InterruptedException e) {
            log.warn(e.getMessage());
        }

        log.info("::::::::::::::::::::::::::: After retrieve orders data");

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
