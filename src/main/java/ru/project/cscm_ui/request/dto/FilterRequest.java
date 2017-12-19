package ru.project.cscm_ui.request.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class FilterRequest {

	private final String filterValue;
	private final Integer id;

	@JsonCreator
	public FilterRequest(@JsonProperty("id") final Integer id, @JsonProperty("filterValue") final String filter) {
		this.filterValue = filter;
		this.id = id;
	}
	
	public String getFilterValue() {
		return filterValue;
	}
	
	public Integer getId() {
		return id;
	}

	@Override
	public String toString() {
		return filterValue;
	}
	
	
}
