package com.controlled.task.context;

/**
 * Маркировочный интерфейс для получения всех настроект для задач.
 * При появлении новой задачи нужно добавить задачу в {@link TaskContextType}.
 */
public interface TaskConfigurationProperties {

    String getCron();
    Integer getExpiredDivider();

}
