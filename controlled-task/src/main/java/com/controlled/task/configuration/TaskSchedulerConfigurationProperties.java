package com.controlled.task.configuration;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("schedule.service")
public class TaskSchedulerConfigurationProperties {

    @Min(10)
    @NotNull
    private Long delay;

}
