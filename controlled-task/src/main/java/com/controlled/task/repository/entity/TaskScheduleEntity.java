package com.controlled.task.repository.entity;

import com.controlled.task.context.TaskContextType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "task_schedule")
public class TaskScheduleEntity extends AbstractPersistable<Long> {

    @Column
    @Enumerated(EnumType.STRING)
    private TaskContextType contextType;
    @Column
    private Timestamp nextExecutionTime;
    @Column
    @UpdateTimestamp
    private Timestamp updateTime;

}
