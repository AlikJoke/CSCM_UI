package ru.project.cscm_ui.commons;

import java.util.Collection;
import java.util.Map;

public interface Loader<T> {

	Collection<T> load(Map<String, Object> params);
}
