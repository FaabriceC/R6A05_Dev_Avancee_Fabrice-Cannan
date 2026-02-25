package com.master.air.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SortValidationAdvice sortValidationAdvice;

    public WebConfig(SortValidationAdvice sortValidationAdvice) {
        this.sortValidationAdvice = sortValidationAdvice;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(sortValidationAdvice);
    }
}