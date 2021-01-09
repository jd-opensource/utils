package com.jd.httpservice;

/**
 * 请求参数过滤器；
 * 
 * @author haiq
 *
 */
public interface RequestParamFilter {
	
	void filter(HttpMethod requestMethod, NamedParamMap requestParams);
	
}
