package com.controlled.task.controller;

import com.controlled.task.repository.TaskScheduleRepository;
import com.controlled.task.repository.entity.TaskScheduleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/controlled-task")
public class TestController {

    private final TaskScheduleRepository taskScheduleRepository;

    @GetMapping("/doSomeShitTaskResults")
    public List<TaskScheduleEntity> getDoSomeShitTaskResults() {
        return taskScheduleRepository.findAll();
    }

}
