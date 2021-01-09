package com.jd.httpservice.agent;

import com.jd.httpservice.HttpMethod;
import com.jd.httpservice.NamedParamMap;
import com.jd.httpservice.RequestParamFilter;

public class NullRequestParamFilter implements RequestParamFilter{
	
	public static RequestParamFilter INSTANCE = new NullRequestParamFilter();
	
	private NullRequestParamFilter() {
	}

	@Override
	public void filter(HttpMethod requestMethod, NamedParamMap requestParams) {
	}

}
