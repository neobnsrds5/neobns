package com.neobns.flowcontrol;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisConfig {

    private final FlowControlProp prop;

    public RedisConfig(FlowControlProp prop) {
        this.prop = prop;
    }

    @Bean
    public RedisMessageListenerContainer redsContainer(RedisConnectionFactory connectionFactory,
                                                       MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter,new PatternTopic("bulkhead-config"));
//        container.addMessageListener(listenerAdapter, new PatternTopic(prop.getChannel()));
        return container;
    }
    @Bean
    public MessageListenerAdapter listenerAdapter(FlowConfigUpdateService service) {
        return new MessageListenerAdapter(service, "updateBulkheadConfig");
    }
}
