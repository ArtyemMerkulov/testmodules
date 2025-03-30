package com.controlled.task.context.doSomething;

public interface DoSomethingTaskContext {

    DoSomethingTaskStateStart toStart();

    DoSomethingTaskStateProcess toProcess();

    DoSomethingTaskStateError toError();

    DoSomethingTaskStateDone toDone();

}
