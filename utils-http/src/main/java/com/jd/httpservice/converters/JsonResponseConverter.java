package com.jd.httpservice.converters;

import java.io.InputStream;

import com.jd.blockchain.utils.serialize.json.JSONSerializeUtils;
import com.jd.httpservice.HttpServiceContext;
import com.jd.httpservice.ResponseConverter;
import com.jd.httpservice.agent.ServiceRequest;

public class JsonResponseConverter implements ResponseConverter {

	private Class<?> clazz;

	public JsonResponseConverter(Class<?> clazz) {
		this.clazz = clazz;
	}

	@Override
	public Object getResponse(ServiceRequest request, InputStream responseStream, HttpServiceContext serviceContext) throws Exception {
		String jsonResponse = (String) StringResponseConverter.INSTANCE.getResponse(request, responseStream, null);
		if (jsonResponse == null) {
			return null;
		}
		jsonResponse = jsonResponse.trim();
		// TODO: 未指定“日期时间”格式的策略；
		return JSONSerializeUtils.deserializeAs(jsonResponse, clazz);
//		return JSON.toJavaObject(JSONObject.parseObject(jsonResponse), clazz);
	}

}
