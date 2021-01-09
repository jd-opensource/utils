package com.jd.httpservice.agent;

import java.net.URI;
import java.nio.ByteBuffer;

import com.jd.httpservice.HttpMethod;
import com.jd.httpservice.NamedParamMap;

public interface ServiceRequest {

	HttpMethod getHttpMethod();

	URI getUri();

	ByteBuffer getBody();

	NamedParamMap getRequestParams();

	/**
	 * 返回服务方法的参数值列表；
	 * 
	 * @return
	 */
	Object[] getArgs();
	
}