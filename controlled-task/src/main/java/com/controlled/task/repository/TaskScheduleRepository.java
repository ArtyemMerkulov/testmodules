package com.controlled.task.repository;

import com.controlled.task.repository.entity.TaskScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskScheduleRepository extends JpaRepository<TaskScheduleEntity, Long> {

}
