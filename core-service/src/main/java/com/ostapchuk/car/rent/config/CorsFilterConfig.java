package com.ostapchuk.car.rent.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;
import java.util.List;

import static java.lang.Boolean.TRUE;

@Configuration
@RequiredArgsConstructor
public class CorsFilterConfig {

    private static final List<String> SINGLETON_STAR = Collections.singletonList("*");

    @Value("${frontend.url}")
    private final String frontendUrl;

    @Bean
    public FilterRegistrationBean<CorsFilter> simpleCorsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", createConfig());
        final FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    private CorsConfiguration createConfig() {
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(TRUE);
        config.setAllowedOrigins(List.of("https://www.sandbox.paypal.com", frontendUrl));
        config.setAllowedMethods(SINGLETON_STAR); // TODO: 04.11.2022  
        config.setAllowedHeaders(SINGLETON_STAR); // TODO: 04.11.2022  
        return config;
    }
}