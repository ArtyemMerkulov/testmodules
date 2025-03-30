package com.controlled.task.repository;

import com.controlled.task.context.TaskStatus;
import com.controlled.task.repository.entity.TaskStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskStatusRepository extends JpaRepository<TaskStatusEntity, UUID> {

    List<TaskStatusEntity> findAllByTaskStatusIn(List<TaskStatus> taskStatuses);

}
