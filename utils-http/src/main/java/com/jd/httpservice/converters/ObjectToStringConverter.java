package com.jd.httpservice.converters;

import com.jd.httpservice.StringConverter;

public class ObjectToStringConverter implements StringConverter {

	@Override
	public String toString(Object param) {
		return param == null ? null : param.toString();
	}

}
