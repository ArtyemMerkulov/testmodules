package com.controlled.task.context.doSomething;

import com.controlled.task.context.AbstractTaskContext;
import com.controlled.task.context.AbstractTaskState;
import com.controlled.task.context.TaskContextType;

public class DoSomethingTaskContextImpl extends AbstractTaskContext implements DoSomethingTaskContext {

    public DoSomethingTaskContextImpl() {
        super(TaskContextType.DO_SOMETHING);
    }

    @Override
    public AbstractTaskState handleState() {
        // Перебираются только те статусы, которые нужны для данного контекста. Переключение статусов делегируется состояниям
        // задачи, т.к. только им извество, что им нужно сделать, и в какой статус перейти при выполнении того или иного блока кода
        // внутри себя. В интерфейсе описаваются методы перехода в состояния, соответствующие текущей задачи.
        return switch (status) {
            case START -> toStart();
            case PROCESS -> toProcess();
            case ERROR -> toError();
            case DONE -> toDone();
            default -> throw new IllegalStateException("Unexpected value: " + status);
        };
    }

    @Override
    public DoSomethingTaskStateStart toStart() {
        return new DoSomethingTaskStateStart(this);
    }

    @Override
    public DoSomethingTaskStateProcess toProcess() {
        return new DoSomethingTaskStateProcess(this);
    }

    @Override
    public DoSomethingTaskStateError toError() {
        return new DoSomethingTaskStateError(this);
    }

    @Override
    public DoSomethingTaskStateDone toDone() {
        return new DoSomethingTaskStateDone(this);
    }

}
