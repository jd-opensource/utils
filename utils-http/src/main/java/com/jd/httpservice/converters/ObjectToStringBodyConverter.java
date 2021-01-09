package com.jd.httpservice.converters;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import com.jd.httpservice.HttpServiceConsts;
import com.jd.httpservice.RequestBodyConverter;

public class ObjectToStringBodyConverter implements RequestBodyConverter {

	@Override
	public void write(Object param, OutputStream out) throws IOException{
		try {
			String text = param.toString();
			byte[] bytes = text.getBytes(HttpServiceConsts.CHARSET);
			out.write(bytes);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}
