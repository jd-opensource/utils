package com.jd.blockchain.utils.web.client;

import java.lang.reflect.Method;

import com.jd.httpservice.HttpAction;
import com.jd.httpservice.ResponseBodyConverterFactory;
import com.jd.httpservice.ResponseConverter;

public class WebResponseConverterFactory implements ResponseBodyConverterFactory{

	@Override
	public ResponseConverter createResponseConverter(HttpAction actionDef, Method mth) {
		return new WebResponseConverter(mth.getReturnType());
	}

}
