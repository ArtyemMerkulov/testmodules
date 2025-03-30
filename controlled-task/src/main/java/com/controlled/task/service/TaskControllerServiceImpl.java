package com.controlled.task.service;

import com.controlled.task.configuration.TaskSchedulerConfigurationProperties;
import com.controlled.task.context.AbstractTaskContext;
import com.controlled.task.context.TaskConfigurationProperties;
import com.controlled.task.context.TaskContextType;
import com.controlled.task.context.TaskStatus;
import com.controlled.task.repository.TaskStatusRepository;
import com.controlled.task.repository.entity.TaskScheduleEntity;
import com.controlled.task.repository.entity.TaskStatusEntity;
import com.controlled.task.util.TaskUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.controlled.task.util.Constants.Time.SYSTEM_ZONE_OFFSET;

@Slf4j
@Service
public class TaskControllerServiceImpl implements TaskControllerService {

    private static final String THREAD_NAME = "task-controller-service-thread";
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    private final TaskSchedulerConfigurationProperties taskSchedulerConfigurationProperties;
    private final TaskStatusRepository taskStatusRepository;
    private final TaskExecutorService taskExecutorService;
    private final Map<TaskContextType, TaskConfigurationProperties> taskProperties;

    private LocalDateTime prev;

    public TaskControllerServiceImpl(TaskSchedulerConfigurationProperties taskSchedulerConfigurationProperties,
                                     TaskStatusRepository taskStatusRepository,
                                     TaskExecutorService taskExecutorService,
                                     List<TaskConfigurationProperties> taskConfigurationProperties) {
        this.taskSchedulerConfigurationProperties = taskSchedulerConfigurationProperties;
        this.taskStatusRepository = taskStatusRepository;
        this.taskExecutorService = taskExecutorService;
        if (taskConfigurationProperties.size() < TaskContextType.values().length) {
            throw new NoSuchElementException("Отсутсвует конфигурационный(ые) класс(ы) настроек для задач.");
        }
        this.taskProperties = taskConfigurationProperties.stream()
                                                         .collect(Collectors.toMap(
                                                                 properties -> TaskContextType.getContextType((Class<? extends TaskConfigurationProperties>) properties.getClass()
                                                                                                                                                                       .getSuperclass()),
                                                                 properties -> properties));
        this.prev = LocalDateTime.now();
    }

    @Override
    public void handleTask(TaskScheduleEntity entity) {
        handleContext(createContext(entity));
    }

    @Override
    public void handleContext(AbstractTaskContext context) {
        taskStatusRepository.saveAndFlush(createTaskStatusEntity(context));
        taskExecutorService.addContext(context);
    }

    @PostConstruct
    private void init() {
        EXECUTOR_SERVICE.execute(() -> {
            Thread.currentThread()
                  .setName(THREAD_NAME);
            while (!Thread.currentThread()
                          .isInterrupted()) {
                if (checkDelay(LocalDateTime.now())) {
                    try {
                        List<TaskStatusEntity> runningStatuses = taskStatusRepository.findAllByTaskStatusIn(TaskStatus.retrieveAllNotTerminated());
                        for (TaskStatusEntity status : runningStatuses) {
                            if (isStatusExpired(status)) {
                                status.setTaskStatus(TaskStatus.EXPIRED);
                                taskStatusRepository.save(status);
                            }
                        }
                    } catch (Exception e) {
                        log.error("Ошибка при работе контроллера задач.", e);
                    }
                }
            }
            log.warn("Поток контроллера задач прерван: [Thread={}].", Thread.currentThread());
        });
    }

    @PreDestroy
    private void destroy() {
        EXECUTOR_SERVICE.shutdown();
    }

    private AbstractTaskContext createContext(TaskScheduleEntity entity) {
        try {
            Constructor<? extends AbstractTaskContext> constructor = entity.getContextType()
                                                                           .getContextClass()
                                                                           .getConstructor();
            AbstractTaskContext taskContext = constructor.newInstance();
            taskContext.setTaskControllerService(this);
            taskContext.setParams();
            return taskContext;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private TaskStatusEntity createTaskStatusEntity(AbstractTaskContext context) {
        TaskStatusEntity taskStatus = new TaskStatusEntity();
        taskStatus.setId(context.getId());
        taskStatus.setContextType(context.getContextType());
        taskStatus.setTaskStatus(context.getStatus());
        return taskStatus;
    }

    private boolean checkDelay(LocalDateTime curr) {
        boolean isDelayed = TimeUnit.MILLISECONDS
                                    .toSeconds(curr.toInstant(SYSTEM_ZONE_OFFSET)
                                                   .toEpochMilli() - prev.toInstant(SYSTEM_ZONE_OFFSET)
                                                                         .toEpochMilli())
                            >= taskSchedulerConfigurationProperties.getDelay();
        if (isDelayed) {
            prev = curr;
        }
        return isDelayed;
    }

    private boolean isStatusExpired(TaskStatusEntity status) {
        TaskConfigurationProperties properties = taskProperties.get(status.getContextType());
        LocalDateTime now = LocalDateTime.now();
        long created = status.getCreated()
                             .getTime();
        Timestamp next = TaskUtils.calculateNextExecutionTimeByCron(properties.getCron(), now);
        long nextByNextMillis = TaskUtils.calculateNextExecutionTimeByCron(properties.getCron(), next.toLocalDateTime())
                                         .getTime();
        return (created + (nextByNextMillis - next.getTime()) / properties.getExpiredDivider()) <= now.toInstant(SYSTEM_ZONE_OFFSET)
                                                                                                      .toEpochMilli();
    }

}
