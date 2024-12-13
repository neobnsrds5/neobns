package com.example.neobns.schedule;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.neobns.repository.ErrorDetailRepository;
import com.example.neobns.dto.ErrorLogDTO;
import com.example.neobns.entity.ErrorDetail;
import com.example.neobns.mapper.ErrorMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

@Component
public class NotificationScheduler {
    private static final Logger log = LoggerFactory.getLogger(NotificationScheduler.class);

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
//    @Autowired
//    private ErrorDetailRepository errorDetailRepository;
    @Autowired
    private ErrorMapper errorMapper;
    private static final long DELAY = 10;
    @Scheduled(fixedDelay = DELAY,timeUnit = TimeUnit.SECONDS)
    public void publishErrorEvent() {
        log.info("Publishing events");
        List<ErrorLogDTO> results = errorMapper.getRecord(DELAY);
        
        results.forEach(dto -> {
        	System.out.println(dto.getEvent_id());
        	simpMessagingTemplate.convertAndSend("/topic/error", dto);});
//        errorDetailRepository.getRecord(DELAY).forEach(model ->simpMessagingTemplate.convertAndSend("/topic/error",model));
    }
}