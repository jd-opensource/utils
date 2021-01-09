package com.jd.httpservice.converters;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import com.jd.blockchain.utils.serialize.json.JSONSerializeUtils;
import com.jd.httpservice.HttpServiceConsts;
import com.jd.httpservice.RequestBodyConverter;

public class JsonBodyConverter implements RequestBodyConverter {
	
	private Class<?> dataType;
	
	public JsonBodyConverter(Class<?> dataType) {
		this.dataType = dataType;
	}

	@Override
	public void write(Object param, OutputStream out) throws IOException{
		String jsonString = JSONSerializeUtils.serializeToJSON(param, dataType);
		try {
			out.write(jsonString.getBytes(HttpServiceConsts.CHARSET));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}
