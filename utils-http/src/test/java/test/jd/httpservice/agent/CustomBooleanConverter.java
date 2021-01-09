package test.jd.httpservice.agent;

import com.jd.httpservice.StringConverter;

public class CustomBooleanConverter implements StringConverter {

	@Override
	public String toString(Object obj) {
		Boolean value = (Boolean) obj;
		return value.booleanValue() ? "1" : "0";
	}

}
