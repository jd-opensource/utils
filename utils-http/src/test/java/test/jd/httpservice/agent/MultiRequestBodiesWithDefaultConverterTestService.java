package test.jd.httpservice.agent;

import com.jd.httpservice.HttpAction;
import com.jd.httpservice.HttpMethod;
import com.jd.httpservice.HttpService;
import com.jd.httpservice.PathParam;
import com.jd.httpservice.RequestBody;
import com.jd.httpservice.RequestParam;
import com.jd.httpservice.converters.BinarySerializeRequestBodyConverter;
import com.jd.httpservice.converters.BinarySerializeResponseConverter;

@HttpService(path = "/test", defaultRequestBodyConverter = BinarySerializeRequestBodyConverter.class, defaultResponseConverter = BinarySerializeResponseConverter.class)
public interface MultiRequestBodiesWithDefaultConverterTestService {
	@HttpAction(path = "/content/{id}/multi", method = HttpMethod.POST)
	public TestData multiContentBodies(@PathParam(name = "id") String id, @RequestParam(name = "name") String name,
			@RequestBody TestData data1, @RequestBody TestData data2);

}
