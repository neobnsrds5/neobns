package com.neobns.flowcontrol;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spider.flowcontrol")
@Getter
@Setter
public class FlowControlProp {
    private String channel = "flowcontrol-default-config";
}
