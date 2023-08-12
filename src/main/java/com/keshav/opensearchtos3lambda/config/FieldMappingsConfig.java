package com.keshav.opensearchtos3lambda.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "fieldMappings")
public class FieldMappingsConfig {
    private Map<String, String> mappings = new HashMap<>();

    public Map<String, String> getMappings() {
        return mappings;
    }
}