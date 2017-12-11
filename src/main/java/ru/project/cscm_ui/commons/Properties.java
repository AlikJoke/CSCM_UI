package ru.project.cscm_ui.commons;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.core.env.Environment;

/**
 * Интерфейс для получения свойств из файлов конфигурации.
 * 
 * @author Alimurad A. Ramazanov
 * @since 28.05.2017
 * @version 1.0.0
 *
 */
public interface Properties {

	/**
	 * Получение некоторого свойства по имени.
	 * <p>
	 * 
	 * @see Environment
	 * @param name
	 *            - имя свойства, не может быть {@code null}.
	 * @return свойство, может быть {@code null}.
	 */
	@Null
	String getProperty(@NotNull @NotEmpty String name);
}
