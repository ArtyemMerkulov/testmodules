package com.controlled.task.service;

import com.controlled.task.context.AbstractTaskContext;

public interface TaskExecutorService {

    void addContext(AbstractTaskContext context);
}
