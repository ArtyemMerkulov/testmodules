package com.controlled.task.context.doSomething;

import com.controlled.task.context.TaskConfigurationProperties;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("schedule.dosomething")
public class DoSomethingConfigurationProperties implements TaskConfigurationProperties {

    @NotBlank
    private String cron;
    @Min(2)
    @Max(5)
    @NotNull
    private Integer expiredDivider = 2;

}
