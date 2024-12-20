package com.example.neobns.config;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.neobns.logging.common.JPALoggingInspector;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class HibernateConfig {
	
	private final JPALoggingInspector jpaLoggingInspector;
	
	@Bean
	public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
		// hibernate 설정에 커스텀 Statement Inspector를 등록. inspect는 쿼리 생성 시 실행됨
		return hibernateProperties->{
			hibernateProperties.put(AvailableSettings.STATEMENT_INSPECTOR, jpaLoggingInspector);
		};
	}

}
