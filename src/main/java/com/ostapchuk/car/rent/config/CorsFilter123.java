package com.ostapchuk.car.rent.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;
import java.util.List;

@Configuration
//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter123 {

//    private static final String OPTIONS_METHOD = "OPTIONS";
//
//    @Override
//    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
//                                    final FilterChain filterChain) throws ServletException, IOException {
//        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
//        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE");
////        response.setHeader("Access-Control-Max-Age", "3600");
////        response.setHeader("Access-Control-Expose-Headers", "Location");
//        response.setHeader("Access-Control-Allow-Headers", "x-requested-with, Authorization, Content-Type");
//        response.setHeader("Access-Control-Allow-Credentials", "true");
//        if (OPTIONS_METHOD.equalsIgnoreCase(request.getMethod())) {
//            response.setStatus(HttpServletResponse.SC_OK);
//        } else {
//            filterChain.doFilter(request, response);
//        }
//    }


    @Bean
    public FilterRegistrationBean<CorsFilter> simpleCorsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("https://www.sandbox.paypal.com", "http://localhost:3000"));
        config.setAllowedMethods(Collections.singletonList("*"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        source.registerCorsConfiguration("/**", config);
        final FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
