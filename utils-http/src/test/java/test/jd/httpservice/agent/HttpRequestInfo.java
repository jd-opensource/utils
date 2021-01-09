package test.jd.httpservice.agent;

import java.util.HashMap;
import java.util.Map;

import com.jd.httpservice.HttpMethod;

public class HttpRequestInfo {
	
	private HttpMethod method;
	
	private Map<String, String[]> parameters;
	
	
	public HttpMethod getMethod() {
		return method;
	}

	public Map<String, String[]> getParameters() {
		return parameters;
	}

	public HttpRequestInfo(HttpMethod method, Map<String, String[]> parameters) {
		this.method = method;
		this.parameters = new HashMap<String, String[]>(parameters);
	}
	
}
