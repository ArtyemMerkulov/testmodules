package com.controlled.task.service;

import com.controlled.task.context.AbstractTaskContext;
import com.controlled.task.repository.entity.TaskScheduleEntity;

public interface TaskControllerService {


    void handleTask(TaskScheduleEntity entity);
    void handleContext(AbstractTaskContext context);

}
