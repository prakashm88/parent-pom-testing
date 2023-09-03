package com.itechgenie.apps.framework.logging.dtos;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.itechgenie.apps.framework.core.utils.AppCommonConstant;

public class MDCLogEventMap2 extends AbstractMap<String, Object> {

	private final Set<String> logAttributes;
	private final Map<String, Object> logDataMap;

	public MDCLogEventMap2() {
		this.logAttributes = Set.of(AppCommonConstant.KEY_AUTH_JWT, AppCommonConstant.KEY_ITGID,
				AppCommonConstant.KEY_ITG_REQUEST_ID, AppCommonConstant.KEY_LEVEL, AppCommonConstant.KEY_MESSAGE,
				AppCommonConstant.KEY_SPANID, AppCommonConstant.KEY_TIMESTAMP, AppCommonConstant.KEY_TRACEID,
				AppCommonConstant.KEY_USERNAME, AppCommonConstant.KEY_CLASS, AppCommonConstant.KEY_LINENO,
				AppCommonConstant.KEY_METHOD, AppCommonConstant.KEY_THREAD);
		this.logDataMap = new HashMap<String, Object>();
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return logDataMap.entrySet();
	}

	@Override
	public Object put(String key, Object value) {
		if (!logAttributes.contains(key)) {
			throw new IllegalArgumentException("Key '" + key + "' is not allowed in this map.");
		}
		return logDataMap.put(key, value);
	}

	@Override
	public Object get(Object key) {
		return logDataMap.get(key);
	}

}
