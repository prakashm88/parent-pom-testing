package com.itechgenie.apps.framework.logging.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class MDCLogEventMap {

	private Long timestamp;
	private String level;
	private String thread;
	private String message;
	private String className;
	private String methodName;
	private Integer lineNo;
	private String username;
	private String authjwt;
	private String traceId;
	private String spanId;
	private String itgid;
	private String itgRequestId;

}
