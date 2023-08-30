package com.itechgenie.apps.framework.core.utils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppCommonUtil {

	private static ObjectMapper objectmapper = new ObjectMapper();

	public static String toJson(Object obj) {
		try {
			objectmapper.registerModule(new JavaTimeModule()); 
			return objectmapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			log.error("Exception in converting to json: " + e.getMessage());
			return null;
		}
	}
	
	public static MultiValueMap<String, Object> getHttpHeaderToMap(HttpHeaders headers) {
		MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			String key = entry.getKey();
			List<Object> values = entry.getValue().stream().map(value -> (Object) value).collect(Collectors.toList());

			multiValueMap.put(key, values);
		}
		log.debug("$$$ Final headers: " + AppCommonUtil.toJson(multiValueMap));
		return multiValueMap;
	}

}
