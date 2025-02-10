package com.neobns.admin.flowcontrol.init;

import com.neobns.admin.flowcontrol.ConfigurationProp;
import com.neobns.admin.flowcontrol.dto.BulkheadConfigDto;
import com.neobns.admin.flowcontrol.mapper.ApplicationMapper;
import com.neobns.admin.flowcontrol.mapper.BulkheadMapper;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
public class BulkheadInitializer {

    private final BulkheadRegistry bulkheadRegistry;
    private final BulkheadMapper mapper;
    private final ConfigurationProp prop;
    private final ApplicationMapper applicationMapper;

    public BulkheadInitializer(BulkheadRegistry bulkheadRegistry,
                               BulkheadMapper mapper,
                               ConfigurationProp prop,
                               ApplicationMapper applicationMapper) {
        this.bulkheadRegistry = bulkheadRegistry;
        this.mapper = mapper;
        this.prop = prop;
        this.applicationMapper = applicationMapper;
    }

    @PostConstruct
    public void init(){
        if (applicationMapper.count(prop.getName()) == 0){
            return;
        }
        long id = applicationMapper.findIdByName(prop.getName());
        List<BulkheadConfigDto> bulkheads = mapper.findAll(id);
        for (BulkheadConfigDto bulkhead : bulkheads) {
            BulkheadConfig bulkheadConfig = BulkheadConfig.custom()
                    .maxConcurrentCalls(bulkhead.getMaxConcurrentCalls())
                    .maxWaitDuration(Duration.ofSeconds(bulkhead.getMaxWaitDuration()))
                    .build();
            bulkheadRegistry.bulkhead(bulkhead.getUrl(), bulkheadConfig);
            Bulkhead bh = bulkheadRegistry.bulkhead(bulkhead.getUrl());
//            System.out.println(bh.getName());
//            System.out.println("max calls: " + bh.getBulkheadConfig().getMaxConcurrentCalls());
//            System.out.println("wait: " + bh.getBulkheadConfig().getMaxWaitDuration().getSeconds());
//            System.out.println("===================================");
        }
    }
}
