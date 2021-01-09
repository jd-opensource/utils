package com.jd.httpservice;

import java.lang.reflect.Method;

public interface ResponseBodyConverterFactory {

	ResponseConverter createResponseConverter(HttpAction actionDef, Method mth);
	
}
