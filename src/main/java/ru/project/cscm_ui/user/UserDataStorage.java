package ru.project.cscm_ui.user;

/**
 * Хранилище данных аутентификации пользователей для формирования заголовка
 * Authorization и использования корректного токена для OAuth2.0 авторизации на
 * сервере.
 * 
 * @see UserData
 * 
 * @author Alimurad A. Ramazanov
 *
 */
public interface UserDataStorage {

	/**
	 * Возвращает данные пользователя по идентификатору сессии, если они есть.
	 * <p>
	 * 
	 * @see UserData
	 * 
	 * @param sessionId
	 *            - идентификатор сессии; не может быть {@code null}.
	 * @return может быть {@code null}, если пользователь не авторизован.
	 */
	UserData getUserData(String sessionId);

	/**
	 * Добавляет информацию о некоторой сессии пользователя в хранилище.
	 * <p>
	 * 
	 * @see UserData
	 * 
	 * @param sessionId
	 *            - идентификатор сессии; не может быть {@code null}.
	 * @param userData
	 *            - объект данных с токеном и идентификатором пользователя; не
	 *            может быть {@code null}.
	 */
	void addUserData(String sessionId, UserData userData);

	/**
	 * Удаляет данные некоторой сессии из хранилища.
	 * <p>
	 * 
	 * @param sessionId - идентификатор сессии; не может быть {@code null}.
	 */
	void deleteUserData(String sessionId);
}
