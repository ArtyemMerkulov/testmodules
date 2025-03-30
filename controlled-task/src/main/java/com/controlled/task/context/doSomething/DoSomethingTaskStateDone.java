package com.controlled.task.context.doSomething;

import com.controlled.task.context.AbstractTaskContext;
import com.controlled.task.context.AbstractTaskState;

public class DoSomethingTaskStateDone extends AbstractTaskState {

    public DoSomethingTaskStateDone(AbstractTaskContext context) {
        super(context);
    }

    @Override
    protected void runTask() throws InterruptedException {
        Thread.sleep(5000);
    }

}
