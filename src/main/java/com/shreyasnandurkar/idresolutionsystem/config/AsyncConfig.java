package com.shreyasnandurkar.idresolutionsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean("analyticsExecutor")
    public Executor analyticsExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("analytics-vt-");
        executor.setVirtualThreads(true);
        return executor;
    }

    @Bean("dashboardExecutor")
    public Executor dashboardExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("dashboard-vt-");
        executor.setVirtualThreads(true);
        return executor;
    }
}