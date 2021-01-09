package com.jd.httpservice.agent;

import java.lang.reflect.Method;

import com.jd.httpservice.HttpAction;
import com.jd.httpservice.ResponseBodyConverterFactory;
import com.jd.httpservice.ResponseConverter;
import com.jd.httpservice.converters.ByteArrayResponseConverter;
import com.jd.httpservice.converters.JsonResponseConverter;
import com.jd.httpservice.converters.StringResponseConverter;

public class DefaultResponseConverterFactory implements ResponseBodyConverterFactory {
	
	public static final DefaultResponseConverterFactory INSTANCE = new DefaultResponseConverterFactory();
	
	private DefaultResponseConverterFactory() {
	}

	@Override
	public ResponseConverter createResponseConverter(HttpAction actionDef, Method mth) {
		Class<?> retnClazz = mth.getReturnType();
		// create default response converter;
		if (byte[].class == retnClazz) {
			return ByteArrayResponseConverter.INSTANCE;
		}
		if (String.class == retnClazz) {
			return StringResponseConverter.INSTANCE;
		}
		
		// TODO:未处理 基本类型、输入输出流；
		return new JsonResponseConverter(retnClazz);
	}

}
