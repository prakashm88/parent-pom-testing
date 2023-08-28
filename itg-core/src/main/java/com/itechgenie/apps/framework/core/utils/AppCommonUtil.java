package com.itechgenie.apps.framework.core.utils;

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

}
