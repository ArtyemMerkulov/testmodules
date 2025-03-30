package com.controlled.task.util;

import org.springframework.scheduling.support.CronExpression;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

public class TaskUtils {

    private TaskUtils() {}

    public static Timestamp calculateNextExecutionTimeByCron(String cron, LocalDateTime time) {
        return Timestamp.valueOf(Objects.requireNonNull(CronExpression.parse(cron)
                                                                      .next(time)));
    }

}
