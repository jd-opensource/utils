package test.jd.httpservice.agent;

import java.io.InputStream;

import com.jd.httpservice.HttpServiceContext;
import com.jd.httpservice.ResponseConverter;
import com.jd.httpservice.agent.ServiceRequest;
import com.jd.httpservice.converters.JsonResponseConverter;

public class GetContentResponseConverterWithException implements ResponseConverter {

	private JsonResponseConverter jsonResponseConverter = new JsonResponseConverter(DataResponse.class);

	@Override
	public Object getResponse(ServiceRequest request, InputStream responseStream, HttpServiceContext serviceContext) throws Exception {
		DataResponse data = (DataResponse) jsonResponseConverter.getResponse(request, responseStream, null);
		if (data.isSuccess()) {
			return data.getContent();
		}
		throw new GetContentException(data.getError());
	}

}
