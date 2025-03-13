package com.hering.desafiojava.api;

import com.hering.desafiojava.api.config.WebMvcConfig;
import com.hering.desafiojava.api.controller.TextToSpeechController;
import com.hering.desafiojava.core.services.TextToSpeechService;
import com.hering.desafiojava.core.ttsProducer.TextToSpeechProducer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ttsWorker.consumer.TextToSpeechConsumer;

@SpringBootApplication
@EntityScan(basePackages = {"com.hering.desafiojava.core.entities"})
@ComponentScan(basePackageClasses = {WebMvcConfig.class, TextToSpeechService.class, TextToSpeechController.class, TextToSpeechProducer.class, TextToSpeechConsumer.class})
@EnableJpaRepositories(basePackages = {"com.hering.desafiojava.core.repository"})
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

}
