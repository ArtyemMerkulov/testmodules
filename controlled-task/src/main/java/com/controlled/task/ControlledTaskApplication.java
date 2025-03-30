package com.controlled.task;

import com.controlled.task.configuration.TaskSchedulerConfigurationProperties;
import com.controlled.task.context.doSomething.DoSomethingConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackageClasses={
        TaskSchedulerConfigurationProperties.class,
        DoSomethingConfigurationProperties.class
})
@EnableScheduling
public class ControlledTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(ControlledTaskApplication.class, args);
    }

}
