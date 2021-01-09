package com.jd.httpservice.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.jd.httpservice.NamedParam;
import com.jd.httpservice.NamedParamMap;

public abstract class RequestUtils {
	
	@SuppressWarnings("unchecked")
	public static List<NameValuePair> createQueryParameters(NamedParamMap queryParams) {
		if (queryParams == null || queryParams.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		NamedParam[] paramValues = queryParams.getParams();
		for (NamedParam param : paramValues) {
			params.add(new BasicNameValuePair(param.getName(), param.getValue()));
		}
		return params;
	}
}
