package ru.project.cscm_ui.request.dto;

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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("filterLoader")
public class FilterRequestLoader implements Loader<FilterRequest> {

	private static final String URI = "/filters";
	
	@Override
	public Collection<FilterRequest> load(final Map<String, Object> params) {
		try {
			final HttpsURLConnection conn = UIHelper.getConnection(URI, (String) params.get("sessionId"));
			if (conn.getResponseCode() == 200) {
				final StringWriter writer = new StringWriter();
				IOUtils.copy(conn.getInputStream(), writer);
				final List<FilterRequest> filters = new ObjectMapper().readValue(writer.toString(), new TypeReference<List<FilterRequest>>() {});
				return filters;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return new ArrayList<>();
	}

}
