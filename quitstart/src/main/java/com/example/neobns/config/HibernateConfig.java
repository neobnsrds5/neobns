package com.example.neobns.config;

import org.springframework.context.annotation.Configuration;

import com.example.neobns.logging.common.JPASqlStatementInspector;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class HibernateConfig {

	private static final JPASqlStatementInspector jpaInspector;

}
