package com.master.air.web;

import com.master.air.model.Annonce;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.*;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

@Component
public class SortValidationAdvice implements HandlerMethodArgumentResolver {

    private final PageableHandlerMethodArgumentResolver delegate = new PageableHandlerMethodArgumentResolver();
    private final Set<String> allowedSortFields = buildAllowedSortFields();

    private Set<String> buildAllowedSortFields() {
        Set<String> blocked = Set.of("author", "category", "version");
        Set<String> allowed = new HashSet<>();
        for (Field f : Annonce.class.getDeclaredFields()) {
            if (!blocked.contains(f.getName())) allowed.add(f.getName());
        }
        return allowed;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Pageable.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {
        Pageable pageable = (Pageable) delegate.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        pageable.getSort().forEach(order -> {
            String property = order.getProperty();
            if (!allowedSortFields.contains(property)) {
                throw new IllegalArgumentException("Invalid sort field: " + property);
            }
        });

        return pageable;
    }
}