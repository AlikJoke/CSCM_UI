package ru.project.cscm_ui.commons;

import org.springframework.beans.factory.annotation.Autowired;

import ru.project.cscm_ui.user.UserDataStorage;

public abstract class AbstractLoader<T> implements Loader<T> {

	@Autowired
	protected UserDataStorage userDataStorage;
	
	
}
