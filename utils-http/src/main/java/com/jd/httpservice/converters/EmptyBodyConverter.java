package com.jd.httpservice.converters;

import java.io.IOException;
import java.io.OutputStream;

import com.jd.httpservice.RequestBodyConverter;

public class EmptyBodyConverter implements RequestBodyConverter{

	@Override
	public void write(Object param, OutputStream out) throws IOException{
		//Do nothing;
	}

}
