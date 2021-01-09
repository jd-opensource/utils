package com.jd.httpservice.converters;

import java.io.InputStream;

import com.jd.httpservice.HttpServiceContext;
import com.jd.httpservice.ResponseConverter;
import com.jd.httpservice.agent.ServiceRequest;

public class NullResponseConverter implements ResponseConverter {
	
	public static final ResponseConverter INSTANCE = new NullResponseConverter();
	
	private NullResponseConverter() {
	}

	@Override
	public Object getResponse(ServiceRequest request, InputStream responseStream, HttpServiceContext serviceContext) {
		return null;
	}

}
