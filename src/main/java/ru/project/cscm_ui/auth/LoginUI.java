package ru.project.cscm_ui.auth;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import ru.project.cscm_ui.commons.AccessTokenHelper;
import ru.project.cscm_ui.commons.Properties;
import ru.project.cscm_ui.commons.UIHelper;
import ru.project.cscm_ui.request.RequestUI;
import ru.project.cscm_ui.user.UserData;
import ru.project.cscm_ui.user.UserDataStorage;

@SpringUI(path = LoginUI.PATH)
@Theme(LoginUI.THEME_NAME)
@Title(LoginUI.TITLE_UI)
public class LoginUI extends UI {

	private static final long serialVersionUID = 8655698055156902998L;

	public final static String PATH = "/SVCM/login";
	final static String TITLE_UI = "CM Supply Chain";
	final static String THEME_NAME = "login";

	private TextField username;
	private PasswordField password;
	private Button loginButton;
	private VerticalLayout content;
	private CheckBox rememberMe;

	@Autowired
	private Properties props;

	@Autowired
	private UserDataStorage userDataStorage;

	public LoginUI() {
		super();
		this.content = new VerticalLayout();
		this.content.setStyleName("loginForm");
		this.username = new TextField("Username");
		this.username.setStyleName("loginPanelInputText");
		this.password = new PasswordField("Password");
		this.password.setStyleName("loginPanelInputText");
		this.loginButton = new Button("Sign in");
		this.loginButton.setStyleName("loginButton");
		this.rememberMe = new CheckBox("Remember me");
	}

	@Override
	protected void init(VaadinRequest request) {
		final String sessionId = request.getWrappedSession().getId();
		if (userDataStorage.getUserData(sessionId) == null) {

			this.username.setPlaceholder("Enter you username");
			this.password.setPlaceholder("Enter you password");

			this.loginButton.addClickListener(click -> {
				if (StringUtils.isEmpty(this.username.getValue()) || StringUtils.isEmpty(this.password.getValue())) {
					throw new IllegalArgumentException("Некорректный логин / пароль");
				}

				final String fullUrl = props.getProperty("server.entrypoint.url") + "?"
						+ AccessTokenHelper.getGrantTypeClientCredentials();
				final HttpsURLConnection conn = UIHelper.getConnection(fullUrl);
				final String authData = this.username.getValue() + ":" + this.password.getValue();
				final String encodedData = Base64.getEncoder()
						.encodeToString(authData.getBytes(Charset.forName("UTF-8")));

				try {
					conn.setDoOutput(true);
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Authorization", "Basic " + encodedData);

					final int responseCode = conn.getResponseCode();
					if (responseCode < 300 && responseCode >= 200) {
						final StringWriter writer = new StringWriter();
						IOUtils.copy(conn.getInputStream(), writer);
						final String token = new ObjectMapper().readTree(writer.toString()).findValue("access_token")
								.asText();
						userDataStorage.addUserData(sessionId, new UserData(token, username.getValue(), encodedData));
					} else {
						throw new IllegalArgumentException("Некорректный логин / пароль");
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				getUI().getPage().setLocation(RequestUI.PATH);
			});

			content.addComponent(this.username);
			content.addComponent(this.password);
			content.addComponent(this.rememberMe);
			content.addComponent(this.loginButton);
			
			content.setComponentAlignment(this.username, Alignment.MIDDLE_CENTER);
			content.setComponentAlignment(this.password, Alignment.MIDDLE_CENTER);
			content.setComponentAlignment(this.rememberMe, Alignment.MIDDLE_CENTER);
			content.setComponentAlignment(this.loginButton, Alignment.MIDDLE_CENTER);

			UIHelper.configureUIException(content).setContent(content);
		} else {
			getUI().getPage().setLocation(RequestUI.PATH);
		}
	}

}