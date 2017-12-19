package ru.project.cscm_ui.request.dto;

import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import ru.project.cscm_ui.commons.Loader;
import ru.project.cscm_ui.commons.UIHelper;
import ru.project.cscm_ui.commons.Uploader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("requestLoader")
public class RequestLoader implements Loader<Request>, Uploader<Request> {

	private static final String URI = "/requests";
	
	@Override
	public Collection<Request> load(Map<String, Object> params) {
		try {
			final Integer filterId = (Integer) params.get("filterId");
			final String uri = filterId == Integer.MAX_VALUE ? URI : URI + "/" + filterId;
			
			final HttpsURLConnection conn = UIHelper.getConnection(uri, (String) params.get("sessionId"));
			if (conn.getResponseCode() == 200) {
				final StringWriter writer = new StringWriter();
				IOUtils.copy(conn.getInputStream(), writer);
				final List<Request> filters = new ObjectMapper().readValue(writer.toString(), new TypeReference<List<Request>>() {});
				return filters;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return new ArrayList<>();
	}

	@Override
	public Request upload(Object data, Map<String, Object> params) {
		final Integer requestId = (Integer) params.get("requestId");
		final String uri = requestId == null ? URI : URI + "/" + requestId;
		final HttpsURLConnection conn = UIHelper.getConnection(uri, (String) params.get("sessionId"));
		if (params.get("content-type") != null) {
			conn.addRequestProperty("Content-Type", (String) params.get("content-type"));
		}
		
		try {
			conn.setRequestMethod((String) params.get("method"));
			if (data != null) {
				conn.setDoOutput(true);
				final OutputStream wr = conn.getOutputStream();
				wr.write(data.toString().getBytes("UTF-8"));
				wr.flush();
				wr.close();
			}
			
			final int responseCode = conn.getResponseCode();
			if (responseCode < 300 && responseCode >= 200 && responseCode != 204) {
				final StringWriter writer = new StringWriter();
				IOUtils.copy(conn.getInputStream(), writer);
				return new ObjectMapper().readValue(writer.toString(), Request.class);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return null;
	}

}
