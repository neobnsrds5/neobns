package com.example.neobns.logging.common;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

public class CustomFileAppender extends FileAppender<ILoggingEvent> {
	private Properties getProps() {
		Properties prop = new Properties();
		prop.setProperty("bootstrap.servers", "localhost:9092");
    	prop.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    	prop.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    	
    	return prop;
	}
	
	@Override
	protected void append(ILoggingEvent eventObject) {
		// TODO Auto-generated method stub
		super.append(eventObject);
		
		try (Producer<String, String> producer = new KafkaProducer<>(getProps())){
			String logMessage = new String(this.encoder.encode(eventObject), StandardCharsets.UTF_8);
			System.out.println(logMessage);
			producer.send(new ProducerRecord<>("logs", logMessage));
		}
	}
	
	@Override
	public void stop() {
		// TODO Auto-generated method stub
		super.stop();
	}
}
