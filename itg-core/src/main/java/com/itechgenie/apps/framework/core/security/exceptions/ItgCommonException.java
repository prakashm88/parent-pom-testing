package com.itechgenie.apps.framework.core.security.exceptions;

import lombok.Data;

@Data
public class ItgCommonException extends Exception {

	public ItgCommonException(String msg) {
		super(msg);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7237985256590784587L;

}
