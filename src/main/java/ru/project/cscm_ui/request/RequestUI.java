package ru.project.cscm_ui.request;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import ru.project.cscm_ui.commons.UIHelper;
import ru.project.cscm_ui.user.UserDataStorage;

@SpringUI(path = RequestUI.PATH)
@Theme(ValoTheme.THEME_NAME)
@Title(RequestUI.TITLE_UI)
public class RequestUI extends UI {

	private static final long serialVersionUID = 8655698055156902998L;

	public final static String PATH = "/SVCM/request";
	final static String TITLE_UI = "CM Supply Chain";

	private VerticalLayout content;
	
	@Autowired
	private UserDataStorage userDataStorage;

	public RequestUI() {
		super();
	}

	@Override
	protected void init(VaadinRequest request) {
		if (userDataStorage.getUserData(request.getWrappedSession().getId()) == null) {
			UIHelper.redirectToLogin();
		}
		
		UIHelper.configureUIException(content).setContent(content);
	}

}