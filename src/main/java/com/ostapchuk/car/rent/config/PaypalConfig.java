package com.ostapchuk.car.rent.config;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.util.Map;

@ConfigurationProperties(prefix = "paypal")
@ConstructorBinding
@RequiredArgsConstructor
public class PaypalConfig {

    private final String clientId;
    private final String clientSecret;
    private final String mode;
    private Map<String, String> config;

    @PostConstruct
    private void initConfig() {
        config = Map.of("mode", mode);
    }

    @Bean
    public OAuthTokenCredential oAuthTokenCredential() {
        return new OAuthTokenCredential(clientId, clientSecret, config);
    }

    @Bean
    public APIContext apiContext() {
        final APIContext context = new APIContext(clientId, clientSecret, mode);
        context.setConfigurationMap(config);
        return context;
    }
}