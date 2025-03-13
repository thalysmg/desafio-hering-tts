package com.hering.desafiojava.core.ttsProducer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TextToSpeechProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(Long textToSpeechId) {
        kafkaTemplate.send("tts-requests", textToSpeechId.toString());
    }
}
