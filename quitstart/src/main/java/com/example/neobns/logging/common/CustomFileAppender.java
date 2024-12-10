package com.example.neobns.logging.common;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.apache.log4j.FileAppender;
import org.apache.log4j.spi.LoggingEvent;

public class CustomFileAppender extends FileAppender {
	
	// Kafka Producer 설정
	private Properties getProps() {
		Properties prop = new Properties();
		prop.setProperty("bootstrap.servers", "localhost:9092");
    	prop.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    	prop.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    	
    	return prop;
	}

	@Override
	public void append(LoggingEvent event) {
		// FileAppender의 기본 동작 실행 (파일로 로그 저장)
		super.append(event);
	
		// Kafka로 로그 메시지 전송
		try (Producer<String, String> producer = new KafkaProducer<>(getProps())) {
			String logMessage = this.layout.format(event); // 로그 메시지를 레이아웃 형식으로 포맷
			System.out.println(logMessage);
			producer.send(new ProducerRecord<>("logs", logMessage));
		} 
	}
	
	@Override
    public void close() {
        // 리소스 정리
        super.close();
    }
}
