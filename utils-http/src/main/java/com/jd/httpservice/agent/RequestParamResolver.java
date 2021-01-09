package com.jd.httpservice.agent;

import com.jd.httpservice.NamedParamMap;

/**
 * 请求参数解析器；
 * 
 * @author haiq
 *
 */
interface RequestParamResolver {
	
	/**
	 * 将方法参数列表解析为请求参数的变量表；
	 * 
	 * @param args 方法参数列表；
	 * @return
	 */
	NamedParamMap resolve(Object[] args);
	
}
