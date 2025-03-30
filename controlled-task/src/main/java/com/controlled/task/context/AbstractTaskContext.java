package com.controlled.task.context;

import com.controlled.task.service.TaskControllerService;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * У наследуемых объектов обязятельно должен быть пустой конструктор
 */
public abstract class AbstractTaskContext {

    @Getter
    private final UUID id = UUID.randomUUID();
    @Getter
    private final TaskContextType contextType;

    @Getter
    @Setter
    private TaskControllerService taskControllerService;

    @Getter
    protected TaskStatus status = TaskStatus.START;

    public AbstractTaskContext(TaskContextType contextType) {
        this.contextType = contextType;
    }

    public void changeStatus(TaskStatus status) {
        this.status = status;
        taskControllerService.handleContext(this);
    }

    public abstract AbstractTaskState handleState();

    public void setParams(Object ...args) {
        // Реализуется в каждом контексте индивидуально, если требуются специфичные для реализации абстрактного класса поля (т.к.
        // контекст создается через пустой конструктор). Ищем в массиве агрументов нужные нам зависимсоти и сетим.
        // По умолчанию ничего не делает.
    }

}
