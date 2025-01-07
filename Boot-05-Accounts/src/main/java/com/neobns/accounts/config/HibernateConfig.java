package com.neobns.accounts.config;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.neobns.accounts.log.JPALoggingInspector;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class HibernateConfig {
	
	private final JPALoggingInspector jpaLoggingInspector;
	
	@Bean
	public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
		return hibernateProperties->{
			hibernateProperties.put(AvailableSettings.STATEMENT_INSPECTOR, jpaLoggingInspector);
		};
	}
}
