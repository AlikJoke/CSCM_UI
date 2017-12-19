package ru.project.cscm_ui.commons;

import java.util.Map;

public interface Uploader<T> {

	T upload(Object data, Map<String, Object> params);
}
