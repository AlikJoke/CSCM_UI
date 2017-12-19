package ru.project.cscm_ui.commons;

public abstract class AccessTokenHelper {

	public static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
	
	public static String getGrantTypeClientCredentials() {
		return "grant_type=" + GRANT_TYPE_CLIENT_CREDENTIALS;
	}
	
	public static String getAccessTokenParam() {
		return "?access_token=";
	}
}
