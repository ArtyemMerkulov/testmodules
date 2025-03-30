package com.controlled.task.repository.entity;

import com.controlled.task.context.TaskContextType;
import com.controlled.task.context.TaskStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "task_status")
public class TaskStatusEntity {

    @Id
    @Column
    private UUID id;
    @Column
    @Enumerated(EnumType.STRING)
    private TaskContextType contextType;
    @Column
    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp created;
    @Column
    @UpdateTimestamp
    private Timestamp updated;

}
