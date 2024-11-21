package com.example.clientOne.endpoint;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

@Endpoint(id = "custom")
@Component
public class CustomActuatorEndpoint {

	@ReadOperation
	public Object customEndPoint(/* String userName */) {

		Map<String, String> map = new HashMap<>();
		map.put("key", "value");
//		map.put("userName", userName);
		return map;

	}

}
