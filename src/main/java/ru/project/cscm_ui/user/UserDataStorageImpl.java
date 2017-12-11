package ru.project.cscm_ui.user;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
@Service
public class UserDataStorageImpl implements UserDataStorage {

	private final Map<String, UserData> storage = new HashMap<>();

	@Override
	public UserData getUserData(final String sessionId) {
		if (StringUtils.isEmpty(sessionId)) {
			throw new IllegalArgumentException("SessionId can't be null or empty!");
		}

		return storage.get(sessionId);
	}

	@Override
	public void addUserData(final String sessionId, final UserData userData) {
		if (StringUtils.isEmpty(sessionId)) {
			throw new IllegalArgumentException("SessionId can't be null or empty!");
		}

		if (userData == null) {
			throw new IllegalArgumentException("UserData can't be null!");
		}
		storage.put(sessionId, userData);
	}

	@Override
	public void deleteUserData(final String sessionId) {
		if (StringUtils.isEmpty(sessionId)) {
			throw new IllegalArgumentException("SessionId can't be null or empty!");
		}

		storage.remove(sessionId);
	}
}
