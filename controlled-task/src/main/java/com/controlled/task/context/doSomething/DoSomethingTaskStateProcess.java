package com.controlled.task.context.doSomething;

import com.controlled.task.context.AbstractTaskContext;
import com.controlled.task.context.AbstractTaskState;
import com.controlled.task.context.TaskStatus;

public class DoSomethingTaskStateProcess extends AbstractTaskState {

    private static Integer counter = 0;

    public DoSomethingTaskStateProcess(AbstractTaskContext context) {
        super(context);
    }

    @Override
    protected void runTask() throws InterruptedException {
        Thread.sleep(5000);
        counter++;
        if (counter % 2 == 0) {
            context.changeStatus(TaskStatus.DONE);
        } else {
            context.changeStatus(TaskStatus.ERROR);
        }
    }

}
