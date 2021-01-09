package com.jd.httpservice.converters;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.httpservice.RequestBodyConverter;

public class InputStreamBodyConverter implements RequestBodyConverter{

	@Override
	public void write(Object param, OutputStream out) throws IOException{
		BytesUtils.copy((InputStream)param, out);
	}

}
