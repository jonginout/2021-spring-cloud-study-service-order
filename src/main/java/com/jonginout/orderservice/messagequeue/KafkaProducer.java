package com.jonginout.orderservice.messagequeue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jonginout.orderservice.dto.OrderDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducer {
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public OrderDto send(String topic, OrderDto orderDto) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(orderDto);
        } catch (Exception e) {
            e.printStackTrace();
        }

        kafkaTemplate.send(topic, jsonInString);
        log.info("Kafka Producer -> " + jsonInString);

        return orderDto;
    }
}
