package com.example.Study.Config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", environmentValue("CLOUDINARY_CLOUD_NAME"));
        config.put("api_key", environmentValue("CLOUDINARY_API_KEY"));
        config.put("api_secret", environmentValue("CLOUDINARY_API_SECRET"));
        config.put("secure", true);
        return new Cloudinary(config);
    }

    private String environmentValue(String name) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? "not-configured" : value;
    }
}
