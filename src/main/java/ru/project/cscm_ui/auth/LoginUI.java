package ru.project.cscm_ui.auth;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import ru.project.cscm_ui.commons.AccessTokenHelper;
import ru.project.cscm_ui.commons.Properties;
import ru.project.cscm_ui.commons.UIHelper;
import ru.project.cscm_ui.request.RequestUI;
import ru.project.cscm_ui.user.UserData;
import ru.project.cscm_ui.user.UserDataStorage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickShortcut;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SpringUI(path = LoginUI.PATH)
@Theme(LoginUI.THEME_NAME)
@Title(LoginUI.TITLE_UI)
public class LoginUI extends UI {

	private static final long serialVersionUID = 8655698055156902998L;

	public final static String PATH = "/SVCM/login";
	final static String TITLE_UI = "CM Supply Chain";
	final static String THEME_NAME = "login";

	private Label loginLabel;
	private Label passwordLabel;
	private TextField username;
	private PasswordField password;
	private Button loginButton;
	private VerticalLayout loginPanel;
	private VerticalLayout content;
	private HorizontalLayout loginComposite;
	private HorizontalLayout passwordComposite;
	private Label headerLabel;
	
	@Autowired
	private Properties props;

	@Autowired
	private UserDataStorage userDataStorage;

	public LoginUI() {
		super();
		this.loginComposite = new HorizontalLayout();
		this.passwordComposite = new HorizontalLayout();
		this.headerLabel = new Label("Cash Supply Chain Management");
		this.headerLabel.setStyleName("loginPanelHeader");
		this.content = new VerticalLayout();
		this.loginPanel = new VerticalLayout();
		this.loginPanel.setStyleName("loginPanel");
		this.content.setStyleName("loginForm");
		this.loginLabel = new Label("Login: ");
		this.loginLabel.setStyleName("loginPanelOutputTextColumn");
		this.loginLabel.setStyleName("loginMarginInfo");
		this.username = new TextField();
		this.username.setStyleName("loginPanelInputText");
		this.passwordLabel = new Label("Password: ");
		this.passwordLabel.setStyleName("loginPanelOutputTextColumn");
		this.password = new PasswordField();
		this.password.setStyleName("loginPanelInputText");
		this.loginButton = new Button("Enter");
		this.loginButton.setStyleName("loginButton");
	}

	@Override
	protected void init(VaadinRequest request) {
		final String sessionId = request.getWrappedSession().getId();
		if (userDataStorage.getUserData(sessionId) == null) {

			this.username.focus();
			
			this.username.addShortcutListener(new ClickShortcut(loginButton, KeyCode.ENTER));
			this.password.addShortcutListener(new ClickShortcut(loginButton, KeyCode.ENTER));

			this.loginButton.addClickListener(click -> {
				if (StringUtils.isEmpty(this.username.getValue()) || StringUtils.isEmpty(this.password.getValue())) {
					throw new IllegalArgumentException("Некорректный логин / пароль");
				}

				final String fullUrl = props.getProperty("server.auth.entrypoint.url") + "?"
						+ AccessTokenHelper.getGrantTypeClientCredentials();
				final HttpsURLConnection conn = UIHelper.getAuthConnection(fullUrl);
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
			
			loginComposite.addComponent(this.loginLabel);
			loginComposite.addComponent(this.username);
			
			passwordComposite.addComponent(this.passwordLabel);
			passwordComposite.addComponent(this.password);

			loginPanel.addComponent(this.loginComposite);
			loginPanel.addComponent(this.passwordComposite);
			loginPanel.addComponent(this.loginButton);
			
			loginPanel.setWidth(400, Unit.PIXELS);
			loginPanel.setHeight(170, Unit.PIXELS);
			
			loginPanel.setComponentAlignment(this.loginComposite, Alignment.MIDDLE_CENTER);
			loginPanel.setComponentAlignment(this.passwordComposite, Alignment.MIDDLE_CENTER);
			loginPanel.setComponentAlignment(this.loginButton, Alignment.MIDDLE_CENTER);
			
			content.addComponents(headerLabel, loginPanel);

			UIHelper.configureUIException(loginPanel, 2, "loginFormMessages").setContent(content);
		} else {
			getUI().getPage().setLocation(RequestUI.PATH);
		}
	}

}