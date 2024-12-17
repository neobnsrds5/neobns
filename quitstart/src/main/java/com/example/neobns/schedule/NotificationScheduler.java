package com.example.neobns.schedule;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.neobns.dto.ErrorLogDTO;
import com.example.neobns.dto.FwkErrorHisDto;
import com.example.neobns.mapper.ErrorMapper;
import com.example.neobns.oraclemapper.OracleMapper;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {
    private static final Logger log = LoggerFactory.getLogger(NotificationScheduler.class);
    
//    private static long maxId = 0;
//    private static boolean isFirst = true;
    private static long time = 0L;
    
    private final SimpMessagingTemplate simpMessagingTemplate;
//    private final ErrorMapper errorMapper;
    private final OracleMapper oracleMapper;
    private static final long DELAY = 10;
    
    @Scheduled(fixedDelay = DELAY,timeUnit = TimeUnit.SECONDS)
    public void publishErrorEvent() {
        log.info("Publishing events");
//        List<ErrorLogDTO> results = errorMapper.getRecord(maxId);
//        if (isFirst && maxId == 0) {
//        	maxId = oracleMapper.getCount();
//        	isFirst = false;
//        }
        
        List<FwkErrorHisDto> results = oracleMapper.getRecords(time);
//        
//        System.out.println(results.get(0).getErrorCode() + " + " + results.get(0).getErrorMessage());
//        System.out.println(results.size());
        
        results.forEach(dto -> {
//        	System.out.println(dto.getErrorCode());
        	time = Math.max(time, Long.parseLong(dto.getErrorOccurDtime()));
        	simpMessagingTemplate.convertAndSend("/topic/error", dto);});
    }
}