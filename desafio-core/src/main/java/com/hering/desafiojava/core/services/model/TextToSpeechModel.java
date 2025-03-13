package com.hering.desafiojava.core.services.model;

import com.hering.desafiojava.core.entities.TextToSpeech;
import com.hering.desafiojava.core.enums.TextToSpeechStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class TextToSpeechModel {
    private Long id;
    private Instant moment;
    private String language;
    private String voice;
    private String text;
    private TextToSpeechStatus status;

    public TextToSpeech toEntity() {
        var entity = new TextToSpeech();

        entity.setMoment(getMoment());
        entity.setLanguage(getLanguage());
        entity.setVoice(getVoice());
        entity.setText(getText());
        entity.setStatus(getStatus());

        return entity;
    }

    public static TextToSpeechModel fromEntity(TextToSpeech entity) {
        var model = new TextToSpeechModel();
        model.setId(entity.getId());
        model.setMoment(entity.getMoment());
        model.setLanguage(entity.getLanguage());
        model.setText(entity.getText());
        model.setStatus(entity.getStatus());
        return model;
    }
}
