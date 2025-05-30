package com.example.Study.Config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CloudinaryConfig {

    @Bean
    public Cloudinary getCloudinary(){
        Map config = new HashMap();
        config.put("cloud_name", "phatle");
        config.put("api_key", "429777199322544");
        config.put("api_secret", "ByYPqvxRGTIq41hjNHMAdq-Lpro");
        config.put("secure", true);
        return new Cloudinary(config);
    }
}
