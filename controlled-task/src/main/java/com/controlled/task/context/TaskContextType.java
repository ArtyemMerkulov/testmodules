package com.controlled.task.context;

import com.controlled.task.context.doSomething.DoSomethingConfigurationProperties;
import com.controlled.task.context.doSomething.DoSomethingTaskContextImpl;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TaskContextType {

    DO_SOMETHING(DoSomethingTaskContextImpl.class, DoSomethingConfigurationProperties.class);

    private final Class<? extends AbstractTaskContext> contextClass;
    private final Class<? extends TaskConfigurationProperties> propertiesClass;

    public static TaskContextType getContextType(Class<? extends TaskConfigurationProperties> propertiesClass) {
        for (TaskContextType taskContextType: TaskContextType.values()) {
            if (taskContextType.getPropertiesClass()
                               .equals(propertiesClass)) {
                return taskContextType;
            }
        }
        return null;
    }

}
