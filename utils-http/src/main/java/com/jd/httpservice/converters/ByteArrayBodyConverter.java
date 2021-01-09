package com.jd.httpservice.converters;

import java.io.IOException;
import java.io.OutputStream;

import com.jd.httpservice.RequestBodyConverter;

public class ByteArrayBodyConverter implements RequestBodyConverter{

	@Override
	public void write(Object param, OutputStream out) throws IOException{
		out.write((byte[])param);
	}

}
