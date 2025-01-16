package com.example.neobns.res;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final BulkheadRegistry bulkheadRegistry;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try{
            String jsonMessage = new String(message.getBody());
            BulkheadMessage bulkheadMessage = objectMapper.readValue(jsonMessage, BulkheadMessage.class);
            BulkheadConfig newConfig = BulkheadConfig.custom()
                    .maxConcurrentCalls(bulkheadMessage.getMaxConcurrentCalls())
                    .maxWaitDuration(Duration.ofMillis(bulkheadMessage.getMaxWaitDuration()))
                    .build();

            Bulkhead newBulkhead = Bulkhead.of("globalBulkhead", newConfig);
            bulkheadRegistry.replace("globalBulkhead", newBulkhead);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
