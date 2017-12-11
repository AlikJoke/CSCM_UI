package ru.project.cscm_ui.user;

/**
 * Представляет из себя описание данных о доступе некоторого пользователя в
 * рамках некоторой сессии с операциям с сервером. Хранит токен для операций с
 * сервером, идентификатор пользователя и зашифрованные данные о авторизации для
 * построения заголовка Authorization.
 * 
 * @author Alimurad A. Ramazanov
 *
 */
public class UserData {

	private final String token;
	private final String id;
	private final String authData;

	public String getToken() {
		return token;
	}

	public String getId() {
		return id;
	}

	public String getAuthData() {
		return authData;
	}

	public UserData(final String token, final String id, final String authData) {
		super();
		this.token = token;
		this.id = id;
		this.authData = authData;
	}

}
