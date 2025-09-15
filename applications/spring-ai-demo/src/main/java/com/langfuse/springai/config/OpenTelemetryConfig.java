package com.langfuse.springai.config;

import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetryConfig {

    /**
     * Provides an AutoConfigurationCustomizer that adds AI span filtering.
     * This integrates directly with OpenTelemetry's auto-configuration system.
     */
    @Bean
    public AutoConfigurationCustomizerProvider aiSpanFilterProvider() {
        return p -> p.addSpanProcessorCustomizer((s, c) -> new AISpanFilterProcessor(s));
    }
}