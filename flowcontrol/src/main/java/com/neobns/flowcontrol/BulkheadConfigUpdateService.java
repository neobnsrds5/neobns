package com.neobns.flowcontrol;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import org.springframework.stereotype.Service;

@Service
public class BulkheadConfigUpdateService {
    private final BulkheadRegistry bulkheadRegistry;
    private final ObjectMapper objectMapper;

    public BulkheadConfigUpdateService(final BulkheadRegistry bulkheadRegistry, final ObjectMapper objectMapper) {
        this.bulkheadRegistry = bulkheadRegistry;
        this.objectMapper = objectMapper;
    }

    public void updateBulkheadConfig(String message) {
        try{
            BulkheadUpdateRequest request = objectMapper.readValue(message, BulkheadUpdateRequest.class);

            bulkheadRegistry.bulkhead("myServiceBulkhead").changeConfig(
                    BulkheadConfig.custom()
                            .maxConcurrentCalls(request.getMaxConcurrentCalls())
                            .build()
            );

            System.out.println("Bulkhead config updated");
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
