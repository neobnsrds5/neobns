package com.neobns.flowcontrol.repository;

import com.neobns.flowcontrol.entity.FlowControlConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FlowControlConfigRepository extends JpaRepository<FlowControlConfig, Long> {
    Optional<FlowControlConfig> findByChannelName(String channelName);
}
