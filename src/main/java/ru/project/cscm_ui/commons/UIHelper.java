package ru.project.cscm_ui.commons;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.project.cscm_ui.auth.LoginUI;
import ru.project.cscm_ui.user.UserData;
import ru.project.cscm_ui.user.UserDataStorage;

import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.WebBrowser;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

/**
 * Интерфейс задает дефолтный конфигуратор обработки ошибок на пользовательских
 * формах.
 * 
 * @author Alimurad A. Ramazanov
 * @since 20.08.2017
 * @version 1.0.0
 *
 */
@Service
public class UIHelper {
	
	private static final String ERROR_ID = "errorLabelId";
	
	private static Properties props;
	private static UserDataStorage userDataStorage;
	
	private UIHelper(@Autowired Properties properties, @Autowired UserDataStorage userStorage) {
		super();
		props = properties;
		userDataStorage = userStorage;
	}

	/**
	 * Дефолтный метод, конфигурирующий обработку ошибок на форме.
	 * <p>
	 * 
	 * @see UI
	 * @param content
	 *            - компонент, на котором будет отображена ошибка; не может быть
	 *            {@code null}.
	 * @return текущую форму.
	 */
	@NotNull
	public static UI configureUIException(@NotNull final AbstractOrderedLayout content, final int index, final String styleName) {
		UI.getCurrent().setErrorHandler(new DefaultErrorHandler() {

			private static final long serialVersionUID = 7896726324438602112L;

			@Override
			public void error(com.vaadin.server.ErrorEvent event) {
				String cause = "";
				for (Throwable t = event.getThrowable(); t != null; t = t.getCause()) {
					if (t.getCause() == null) {
						cause += t.getMessage();
					}
				}
				
				final Label error = new Label(cause);
				error.setStyleName(styleName);
				error.setId(ERROR_ID);
				
				final Component prevComponent = content.getComponentCount() <= index + 1 ? null : content.getComponent(index);
				
				if (prevComponent == null) {
					content.setHeight(content.getHeight() + error.getHeight(), Unit.PIXELS);
					content.addComponent(error, index);
				} else {
					content.replaceComponent(prevComponent, error);
				}
				
				content.setComponentAlignment(error, Alignment.MIDDLE_CENTER);
				doDefault(event);
			}
		});

		return UI.getCurrent();
	}

	@NotNull
	public static String getUserAgent() {
		final WebBrowser browser = UI.getCurrent().getPage().getWebBrowser();
		if (browser.isChrome()) {
			return "Chrome";
		} else if (browser.isEdge()) {
			return "Edge";
		} else if (browser.isIE()) {
			return "IE";
		} else if (browser.isSafari()) {
			return "Safari";
		} else if (browser.isFirefox()) {
			return "Firefox";
		} else {
			return browser.getBrowserApplication();
		}
	}

	static {
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}

		});
	}
	
	public static void redirectToLogin() {
		UI.getCurrent().getPage().setLocation(LoginUI.PATH);
	}

	public static HttpsURLConnection getAuthConnection(@NotNull final String url) {
		try {
			final URL obj = new URL(url);
			final HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			con.setRequestProperty("User-Agent", UIHelper.getUserAgent());
			return con;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static HttpsURLConnection getConnection(@NotNull final String uri,
			final String sessionId) {
		try {
			final UserData userData = userDataStorage.getUserData(sessionId);
			final String url = props.getProperty("server.entrypoint.url")
					+ uri
					+ AccessTokenHelper.getAccessTokenParam()
					+ userData.getToken();
			final URL obj = new URL(url);
			final HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			con.setRequestProperty("Authorization", "Basic " + userData.getAuthData());
			con.setRequestProperty("User-Agent", UIHelper.getUserAgent());
			return con;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
