package com.example.neobns.schedule;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.neobns.dto.ErrorLogDTO;
import com.example.neobns.mapper.ErrorMapper;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {
    private static final Logger log = LoggerFactory.getLogger(NotificationScheduler.class);
    
    private static long maxId = 0;

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ErrorMapper errorMapper;
    
    private static final long DELAY = 3;
    @Scheduled(fixedDelay = DELAY,timeUnit = TimeUnit.SECONDS)
    public void publishErrorEvent() {
        log.info("Publishing events");
        List<ErrorLogDTO> results = errorMapper.getRecord(maxId);
        
        results.forEach(dto -> {
        	maxId = Math.max(maxId, dto.getEvent_id());
        	simpMessagingTemplate.convertAndSend("/topic/error", dto);});
    }
}