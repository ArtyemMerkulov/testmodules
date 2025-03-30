package com.controlled.task.context.doSomething;

import com.controlled.task.context.AbstractTaskContext;
import com.controlled.task.context.AbstractTaskState;

public class DoSomethingTaskStateError extends AbstractTaskState {

    public DoSomethingTaskStateError(AbstractTaskContext context) {
        super(context);
    }

    @Override
    protected void runTask() throws InterruptedException {
        Thread.sleep(5000);
    }

}
