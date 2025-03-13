package com.hering.desafiojava.core.entities;

import com.hering.desafiojava.core.enums.TextToSpeechStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "text_to_speech")
public class TextToSpeech {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "moment", nullable = false)
    private Instant moment;

    @Column(name = "language", nullable = false)
    private String language;

    @Column(name = "voice", nullable = false)
    private String voice;

    @Column(name = "text", nullable = false)
    private String text;

    @Enumerated(STRING)
    @Column(name = "status", nullable = false)
    private TextToSpeechStatus status;

    @Column(name = "error_text")
    private String errorText;

    @Column(name = "audio_wav_bytes")
    private byte[] audioWavBytes;
}
