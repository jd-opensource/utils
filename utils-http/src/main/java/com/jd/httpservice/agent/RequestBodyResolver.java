package com.jd.httpservice.agent;

import java.io.OutputStream;

interface RequestBodyResolver {
	
	void resolve(Object[] args, OutputStream out);
	
}
