package com.controlled.task.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum TaskStatus {

    START(false),
    PROCESS(false),
    DONE(true),
    ERROR(true),
    EXPIRED(true);

    private final boolean isTerminated;

    public static List<TaskStatus> retrieveAllNotTerminated() {
        return Arrays.stream(TaskStatus.values())
                     .filter(status -> !status.isTerminated())
                     .collect(Collectors.toList());
    }

}
