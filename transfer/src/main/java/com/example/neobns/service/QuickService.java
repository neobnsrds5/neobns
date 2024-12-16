package com.example.neobns.service;

import com.example.neobns.dto.ItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuickService {
    
    private final Environment environment;
    private Map<String, String> cachedProperties = new HashMap<>();

    public Map<String, String> loadCurrentProperties() {
        Map<String, String> currentProperties = new HashMap<>();

        if (environment instanceof ConfigurableEnvironment configurableEnvironment) {
        	
            for (PropertySource<?> propertySource : configurableEnvironment.getPropertySources()) {
                String propertySourceName = propertySource.getName();
                
                if (propertySourceName != null && propertySourceName.startsWith("configserver:")) {
                    if (propertySource.getSource() instanceof Map<?, ?> sourceMap) {
                        sourceMap.forEach((key, value) -> {
                            String stringKey = key.toString();
                            String stringValue = value != null ? value.toString() : null;
                            currentProperties.put(stringKey, stringValue);
                        });
                    }
                }
            }
        }

        return currentProperties;
    }

    public Map<String, String> checkForChanges() {
        Map<String, String> changedProperties = new HashMap<>();

        if (cachedProperties.isEmpty()) {
            cachedProperties = loadCurrentProperties();
            changedProperties.put("status", "초기 상태 저장 완료.");
            return changedProperties;
        }

        Map<String, String> currentProperties = loadCurrentProperties();

        // 변경된 값 탐색
        currentProperties.forEach((key, value) -> {
            if (!cachedProperties.containsKey(key) || !cachedProperties.get(key).equals(value)) {
                changedProperties.put(key, value);
            }
        });

        cachedProperties = currentProperties;

        if (changedProperties.isEmpty()) {
            changedProperties.put("status", "변동 사항이 없습니다.");
        } else {
            changedProperties.put("status", "위와 같은 변경 사항이 있습니다.");
        }

        return changedProperties;
    }
    
}
