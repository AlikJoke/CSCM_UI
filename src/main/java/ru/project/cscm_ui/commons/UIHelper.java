package ru.project.cscm_ui.commons;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.validation.constraints.NotNull;

import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.WebBrowser;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import ru.project.cscm_ui.auth.LoginUI;

/**
 * Интерфейс задает дефолтный конфигуратор обработки ошибок на пользовательских
 * формах.
 * 
 * @author Alimurad A. Ramazanov
 * @since 20.08.2017
 * @version 1.0.0
 *
 */
public abstract class UIHelper {

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
	public static UI configureUIException(@NotNull final VerticalLayout content) {
		UI.getCurrent().setErrorHandler(new DefaultErrorHandler() {

			private static final long serialVersionUID = 7896726324438602112L;

			@Override
			public void error(com.vaadin.server.ErrorEvent event) {
				String cause = "";
				for (Throwable t = event.getThrowable(); t != null; t = t.getCause()) {
					if (t.getCause() == null) {
						cause += t.getMessage() + "<br/>";
					}
				}

				new Notification(null, cause, Notification.Type.ERROR_MESSAGE, true).show(UI.getCurrent().getPage());
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

	public static HttpsURLConnection getConnection(@NotNull final String url) {
		try {
			final URL obj = new URL(url);
			final HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			con.setRequestProperty("User-Agent", UIHelper.getUserAgent());
			return con;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
