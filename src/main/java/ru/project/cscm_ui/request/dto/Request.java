package ru.project.cscm_ui.request.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class Request {

	private final Integer id;
	private final FilterRequest filter;
	private String descx;
	private Date requestDate;
	private boolean isSended;

	public void setDescx(String descx) {
		this.descx = descx;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	@JsonIgnore
	public void setSended(boolean isSended) {
		this.isSended = isSended;
	}

	public Integer getId() {
		return id;
	}

	public FilterRequest getFilter() {
		return filter;
	}

	public String getDescx() {
		return descx;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public boolean isSended() {
		return isSended;
	}

	@JsonCreator
	public Request(@JsonProperty("id") final Integer id, @JsonProperty("filter") final FilterRequest filter,
			@JsonProperty("descx") final String descx, @JsonProperty("requestDate") final Date requestDate, @JsonProperty("isSended") final boolean isSended) {
		super();
		this.id = id;
		this.filter = filter;
		this.requestDate = requestDate;
		this.isSended = isSended;
		this.descx = descx;
	}

}
