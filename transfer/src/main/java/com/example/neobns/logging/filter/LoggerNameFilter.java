package com.example.neobns.logging.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class LoggerNameFilter extends Filter<ILoggingEvent>{
	
	private String loggerName;
	
	public void setLoggerName(String loggerName) {
		this.loggerName = loggerName;
	}

	@Override
	public FilterReply decide(ILoggingEvent event) {
		if(event.getLoggerName().contains(loggerName)) {
			return FilterReply.ACCEPT;
		}
		return FilterReply.DENY;
	}

}
