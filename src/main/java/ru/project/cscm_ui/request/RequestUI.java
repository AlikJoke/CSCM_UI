package ru.project.cscm_ui.request;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import ru.project.cscm_ui.commons.LabelImageSource;
import ru.project.cscm_ui.commons.Loader;
import ru.project.cscm_ui.commons.UIHelper;
import ru.project.cscm_ui.commons.Uploader;
import ru.project.cscm_ui.request.dto.FilterRequest;
import ru.project.cscm_ui.request.dto.Request;
import ru.project.cscm_ui.user.UserDataStorage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.ValueProvider;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;



@SpringUI(path = RequestUI.PATH)
@Theme(RequestUI.THEME)
@Title(RequestUI.TITLE_UI)
public class RequestUI extends UI {

	private static final long serialVersionUID = 8655698055156902998L;

	public final static String PATH = "/SVCM/request";
	final static String TITLE_UI = "CM Supply Chain";
	static final String THEME = "request";

	private VerticalLayout content;
	private HorizontalLayout authComposite;
	private ComboBox<FilterRequest> filters;
	private Label header;
	private Grid<Request> requestsTable;
	private Button logout;
	private Button addButton;
	private Label username;
	private Image bpcImage;
	
	@Autowired
	private UserDataStorage userDataStorage;
	
	@Resource(name = "filterLoader")
	private Loader<FilterRequest> filterLoader;
	
	@Resource(name = "requestLoader")
	private Loader<Request> requestLoader;
	
	@Resource(name = "requestLoader")
	private Uploader<Request> requestUploader;

	private List<FilterRequest> filterRequests = new ArrayList<>();
	
	public RequestUI() {
		super();
		this.filters = new ComboBox<>();
		this.requestsTable = new Grid<>(Request.class);
		this.logout = new Button("Logout");
		this.username = new Label();
		this.authComposite = new HorizontalLayout();
		this.addButton = new Button("Add");
		this.content = new VerticalLayout();
		this.header = new Label("Cash Supply Chain Management");
		this.bpcImage = new Image(null, new StreamResource(new LabelImageSource(), "bpc_white.png"));
	}
	
	private List<FilterRequest> getFilterRequests(final String sessionId) {
		if (this.filterRequests.isEmpty()) {
			final Map<String, Object> params = new HashMap<>();
			params.put("sessionId", sessionId);

			filterRequests.add(0, new FilterRequest(Integer.MAX_VALUE, "All"));
			filterRequests.addAll(filterLoader.load(params));
		}
		return filterRequests;
	}

	@Override
	protected void init(VaadinRequest request) {
		final String sessionId = request.getWrappedSession().getId();
		if (userDataStorage.getUserData(sessionId) == null) {
			UIHelper.redirectToLogin();
		}
		
		header.setStyleName("headerLabel");
		
		this.username.setValue("Logged as " + userDataStorage.getUserData(sessionId).getId());
		this.logout.setStyleName("link");
		this.logout.addClickListener(click -> {
			try {
				if (UIHelper.getConnection("/exit", sessionId).getResponseCode() == 200) {
					userDataStorage.deleteUserData(sessionId);
					UIHelper.redirectToLogin();
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		
		this.authComposite.addComponents(bpcImage, username, logout);
		this.authComposite.setStyleName("headerLayout");
		this.bpcImage.setStyleName("labelImageStyle");
		this.bpcImage.setHeight("50px");
		this.username.setStyleName("labelUsername");
		this.logout.setStyleName("buttonLogout");
		this.authComposite.setComponentAlignment(username, Alignment.MIDDLE_LEFT);
		
		filters.setItems(getFilterRequests(sessionId));
		filters.setSelectedItem(getFilterRequests(sessionId).get(0));
		filters.setEmptySelectionAllowed(false);
		
		final Map<String, Object> params = new HashMap<>();
		params.put("sessionId", sessionId);
		params.put("filterId", Integer.MAX_VALUE);
		
		filters.addSelectionListener(select -> {
			params.put("filterId", filters.getSelectedItem().get().getId());
			requestsTable.setItems(requestLoader.load(params));
		});
		
		refreshTable(params);
		
		addButton.addClickListener(click -> {
			final Window window = getPopupWindow("Add request", params);
			window.setPosition(Math.round(getHeight() / 3), Math.round(getWidth() * 6));
	        addWindow(window);
		});
		
		content.addComponents(authComposite, header, filters, requestsTable, addButton);
		content.setComponentAlignment(authComposite, Alignment.TOP_CENTER);
		
		UIHelper.configureUIException(content, 1, null).setContent(content);
	}
	
	private Window getPopupWindow(final String title, final Map<String, Object> params) {
		final Window subWindow = new Window(title);
        final VerticalLayout subContent = new VerticalLayout();
        final ComboBox<FilterRequest> filters = new ComboBox<>();
        final List<FilterRequest> filterRequests = getFilterRequests((String) params.get("sessionId"));
		filters.setItems(filterRequests.subList(1, filterRequests.size()));
		
		final DateField requestDate = new DateField();
		requestDate.setRequiredIndicatorVisible(true);
		requestDate.setValue(LocalDate.now());
		
		 final HorizontalLayout filterDateComposite = new HorizontalLayout();
		 filterDateComposite.addComponents(filters, requestDate);
		
		final Label descxLabel = new Label("Description: ");
		final TextField descx = new TextField();
		
        final HorizontalLayout descxComposite = new HorizontalLayout();
        descx.setWidth(285, Unit.PIXELS);
        descxComposite.addComponents(descxLabel, descx);
        
        final boolean editMode = params.get("request") != null;
        if (editMode) {
        	final Request request = (Request) params.get("request");
        	requestDate.setValue(request.getRequestDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        	descx.setValue(request.getDescx());
        	filters.setSelectedItem(request.getFilter());
        }
        
        final Button saveButton = new Button("Save");
        saveButton.addClickListener(clickSave -> {
			final Request newReq = new Request(null, filters.getSelectedItem().get(), descx.getValue(), Date
					.valueOf(requestDate.getValue()), false);
			params.put("content-type", "application/json");
			params.put("method", editMode ? "PUT" : "POST");
			try {
				requestUploader.upload(new ObjectMapper().writeValueAsString(newReq), params);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
			requestsTable.setItems(requestLoader.load(params));
			subWindow.close();
        });
        
        final Button closeButton = new Button("Cancel");
        closeButton.addClickListener(closeClick -> subWindow.close());
        
        final HorizontalLayout buttonsComposite = new HorizontalLayout();
        buttonsComposite.addComponents(saveButton, closeButton);
        
        subWindow.setContent(subContent);
        
        subContent.addComponents(filterDateComposite, descxComposite);
        
        final HorizontalLayout nominalComposite = new HorizontalLayout();
		nominalComposite.setVisible(false);
		final HorizontalLayout valComposite = new HorizontalLayout();
		valComposite.setVisible(false);
		final HorizontalLayout countComposite = new HorizontalLayout();
		countComposite.setVisible(false);
		 
		 filters.addSelectionListener(select -> {
			 nominalComposite.removeAllComponents();
			 valComposite.removeAllComponents();
			 countComposite.removeAllComponents();
			 
			 final boolean isCash = filters.getSelectedItem().get().getId() == 0;
			 nominalComposite.setVisible(isCash); 
			 valComposite.setVisible(isCash);
			 countComposite.setVisible(isCash);
			 if (isCash) {
				final Label valuteLabel = new Label("Valute: ");
				valuteLabel.setWidth(185, Unit.PIXELS);
				final ComboBox<String> valutes = new ComboBox<>();
				valutes.setItems("RUR", "EUR");
				valutes.setSelectedItem("RUR");
				valutes.setEmptySelectionAllowed(false);
				valComposite.addComponents(valuteLabel, valutes);
				
				final Label nominalLabel = new Label("Nominal: ");
				nominalLabel.setWidth(185, Unit.PIXELS);
				final ComboBox<String> nominals = new ComboBox<>();
				nominals.setItems("100", "200", "500", "1000", "5000");
				nominals.setSelectedItem("1000");
				nominals.setEmptySelectionAllowed(false);
				nominalComposite.addComponents(nominalLabel, nominals);
				
				final Label countLabel = new Label("Count: ");
				countLabel.setWidth(185, Unit.PIXELS);
				final ComboBox<String> counts = new ComboBox<>();
				counts.setItems("10", "20", "30", "40", "50");
				counts.setSelectedItem("20");
				counts.setEmptySelectionAllowed(false);
				countComposite.addComponents(countLabel, counts);
				
				subContent.addComponent(valComposite, 2);
				subContent.addComponent(nominalComposite, 3);
				subContent.addComponent(countComposite, 4);
			}
		 });
		 
		filters.setSelectedItem(filterRequests.get(1));
		filters.setEmptySelectionAllowed(false);
		
        if (nominalComposite.isVisible()) {
        	subContent.addComponents(valComposite, nominalComposite, countComposite);
        }
        
        subContent.addComponent(buttonsComposite);
        
        subWindow.setResizable(false);
        return subWindow;
	}

	private Component buildSendButton(final Request request, final Map<String, Object> params) {
		if (request.isSended()) {
			return new Label();
		} else {
			final Button button = new Button("Send");
			button.addStyleName(ValoTheme.BUTTON_SMALL);
			button.addClickListener(e -> {
				params.put("content-type", "application/json");
				params.put("method", "PUT");
				params.put("requestId", request.getId());
				
				request.setSended(true);
				try {
					requestUploader.upload(new ObjectMapper().writeValueAsString(request), params);
					refreshTable(params);
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			});

			return button;
		}
    }
	
	private Component buildEditButton(final Request request, final Map<String, Object> params) {
		if (request.isSended()) {
			return new Label();
		} else {
			final Button button = new Button("Edit");
			button.addStyleName(ValoTheme.BUTTON_SMALL);
			button.addClickListener(e -> {
				params.put("request", request);
				params.put("requestId", request.getId());
				final Window window = getPopupWindow("Edit request", params);
				window.setPosition(Math.round(getHeight() / 3), Math.round(getWidth() * 6));
		        addWindow(window);
			});

			return button;
		}
    }

	private Component buildDeleteButton(final Request request, final Map<String, Object> params) {
		if (request.isSended()) {
			return new Label();
		} else {
			final Button button = new Button("Remove");
			button.addStyleName(ValoTheme.BUTTON_SMALL);
			button.addClickListener(e -> {
				params.put("method", "DELETE");
				params.put("requestId", request.getId());
				
				try {
					requestUploader.upload(null, params);
					refreshTable(params);
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			});

			return button;
		}
    }
	
	private void refreshTable(final Map<String, Object> params) {
		requestsTable.removeAllColumns();
		requestsTable.setSizeFull();
		requestsTable.addColumn(Request::getId).setCaption("Id").setMaximumWidth(75)
				.setMinimumWidthFromContent(true).setResizable(false);
		requestsTable.addColumn(new ValueProvider<Request, String>() {

			private static final long serialVersionUID = 1433939982889021991L;

			@Override
			public String apply(Request source) {
				return source.getFilter().getFilterValue();
			}
			
		}).setCaption("Type").setResizable(false);
		requestsTable.addColumn(Request::getDescx).setCaption("Description").setResizable(true);
		requestsTable.addComponentColumn(new ValueProvider<Request, Component>() {

			private static final long serialVersionUID = 1933762846986039686L;

					@Override
					public Component apply(Request source) {
						return buildSendButton(source, params);
					}

				}).setCaption("").setMaximumWidth(100).setResizable(false);
		
		requestsTable.addComponentColumn(new ValueProvider<Request, Component>() {

			private static final long serialVersionUID = 1933762846986039686L;

					@Override
					public Component apply(Request source) {
						return buildEditButton(source, params);
					}

				}).setCaption("").setMaximumWidth(100).setResizable(false);
		
		requestsTable.addComponentColumn(new ValueProvider<Request, Component>() {

			private static final long serialVersionUID = 1933762846986039686L;

					@Override
					public Component apply(Request source) {
						return buildDeleteButton(source, params);
					}

				}).setCaption("").setMaximumWidth(125).setResizable(false);
		
		requestsTable.setItems(requestLoader.load(params));
	}
}