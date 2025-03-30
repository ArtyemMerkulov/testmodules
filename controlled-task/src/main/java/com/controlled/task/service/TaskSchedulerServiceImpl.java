package com.controlled.task.service;

import com.controlled.task.configuration.TaskSchedulerConfigurationProperties;
import com.controlled.task.context.TaskConfigurationProperties;
import com.controlled.task.context.TaskContextType;
import com.controlled.task.repository.TaskScheduleRepository;
import com.controlled.task.repository.entity.TaskScheduleEntity;
import com.controlled.task.util.TaskUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
public class TaskSchedulerServiceImpl implements TaskSchedulerService {

    private static final String THREAD_NAME = "task-scheduler-service-thread";
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    private final TaskSchedulerConfigurationProperties taskSchedulerConfigurationProperties;
    private final TaskScheduleRepository taskScheduleRepository;
    private final TaskControllerService taskControllerService;
    private final Map<TaskContextType, TaskConfigurationProperties> taskProperties;

    private LocalDateTime prev;

    public TaskSchedulerServiceImpl(TaskSchedulerConfigurationProperties taskSchedulerConfigurationProperties,
                                    TaskScheduleRepository taskScheduleRepository,
                                    TaskControllerService taskControllerService,
                                    List<TaskConfigurationProperties> taskConfigurationProperties) {
        this.taskSchedulerConfigurationProperties = taskSchedulerConfigurationProperties;
        this.taskScheduleRepository = taskScheduleRepository;
        this.taskControllerService = taskControllerService;
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

    @PostConstruct
    private void init() {
        EXECUTOR_SERVICE.execute(() -> {
            Thread.currentThread()
                  .setName(THREAD_NAME);
            while (!Thread.currentThread()
                          .isInterrupted()) {
                if (checkDelay(LocalDateTime.now())) {
                    try {
                        List<TaskScheduleEntity> entities = taskScheduleRepository.findAll();
                        initSchedule(entities);

                        for (TaskScheduleEntity entity : entities) {
                            if (isDeadline(entity)) {
                                taskControllerService.handleTask(entity);
                                entity.setNextExecutionTime(calculateNextExecutionTime(entity.getContextType()));
                                taskScheduleRepository.save(entity);
                            }
                        }
                    } catch (Exception e) {
                        log.error("Ошибка при работе обработчике расписания задач.", e);
                    }
                }
            }
            log.warn("Поток обработчика расписания задач прерван: [Thread={}].", Thread.currentThread());
        });
    }

    @PreDestroy
    private void destroy() {
        EXECUTOR_SERVICE.shutdown();
    }

    // Создаем записи с нуля если их нет.
    private void initSchedule(List<TaskScheduleEntity> initialEntities) {
        if (initialEntities.size() < TaskContextType.values().length) {
            for (TaskContextType contextType : TaskContextType.values()) {
                if (initialEntities.stream()
                            .noneMatch(initialEntity -> contextType.equals(initialEntity.getContextType()))) {
                    TaskScheduleEntity entity = new TaskScheduleEntity();
                    entity.setContextType(contextType);
                    entity.setNextExecutionTime(calculateNextExecutionTime(contextType));
                   taskScheduleRepository.save(entity);
                }
            }
        }
    }

    private Timestamp calculateNextExecutionTime(TaskContextType contextType) {
        return TaskUtils.calculateNextExecutionTimeByCron(taskProperties.get(contextType)
                                                                        .getCron(),
                                                          LocalDateTime.now());
    }

    private boolean checkDelay(LocalDateTime curr) {
        boolean isDelayed =  TimeUnit.MILLISECONDS
                                     .toSeconds(curr.toInstant(SYSTEM_ZONE_OFFSET)
                                                    .toEpochMilli() - prev.toInstant(SYSTEM_ZONE_OFFSET)
                                                                          .toEpochMilli())
                             >= taskSchedulerConfigurationProperties.getDelay();
        if (isDelayed) {
            prev = curr;
        }
        return isDelayed;
    }

    private boolean isDeadline(TaskScheduleEntity entity) {
        return entity.getNextExecutionTime()
                     .compareTo(Timestamp.valueOf(LocalDateTime.now())) <= 0;
    }

}
