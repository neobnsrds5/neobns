package com.example.neobns.logging.common;

import java.util.Properties;
//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.Producer;
//import org.apache.kafka.clients.producer.ProducerRecord;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
 
public class CustomFileAppender extends FileAppender<ILoggingEvent> {
//	private Properties getProps() {
//		Properties prop = new Properties();
//		prop.setProperty("bootstrap.servers", "localhost:9092");
//    	prop.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//    	prop.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//    	
//    	return prop;
//	}
	
	// rolling appender 추가 해 각각 분단위로 할 것
	
	@Override
	protected void append(ILoggingEvent eventObject) {

		super.append(eventObject);
		
//		try (Producer<String, String> producer = new KafkaProducer<>(getProps())){
//			String logMessage = new String(this.encoder.encode(eventObject), StandardCharsets.UTF_8);
//			System.out.println(logMessage);
//			if (!logMessage.contains("clearAllAccountCache")) {
//				producer.send(new ProducerRecord<>("logs", logMessage));
//			}
//		}
	}
	
	@Override
	public void stop() {
		// TODO Auto-generated method stub
		super.stop();
	}
}
