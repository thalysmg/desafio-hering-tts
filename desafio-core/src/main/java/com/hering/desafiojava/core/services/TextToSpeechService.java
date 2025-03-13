package com.hering.desafiojava.core.services;

import com.hering.desafiojava.core.entities.TextToSpeech;
import com.hering.desafiojava.core.enums.TextToSpeechStatus;
import com.hering.desafiojava.core.exception.BadRequestException;
import com.hering.desafiojava.core.exception.RecordNotFoundException;
import com.hering.desafiojava.core.repository.TextToSpeechRepository;
import com.hering.desafiojava.core.services.model.TextToSpeechModel;
import com.hering.desafiojava.core.ttsProducer.TextToSpeechProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class TextToSpeechService {

    public static final String LANGUAGE_NOT_SUPPORTED = "ERROR: The language does not support!";

    private final TextToSpeechRepository repository;
    private final TextToSpeechProducer producer;

    private final static HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build();

    @Transactional
    public TextToSpeechModel createSpeech(String language, String voice, String text) throws BadRequestException {
        String url = "http://api.voicerss.org/?key=b52ad1e1a7ea44e799babb04ff4d229f&hl=" + language + "&v=" + voice + "&src=" + encodeValue(text);
        try {
            HttpRequest request = HttpRequest.newBuilder()
//                .header("AuthenticationToken", apiUserToken)
                .uri(new URI(url))
                .GET()
                .build();

            var httpResponse = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());

            byte[] responseBytes = httpResponse.body().readAllBytes();

            String bodyString = new String(responseBytes, StandardCharsets.UTF_8);
            if (httpResponse.statusCode() != 200 || LANGUAGE_NOT_SUPPORTED.equals(bodyString)) {
                throw new BadRequestException(bodyString);
            }

            var model = new TextToSpeechModel();
            model.setMoment(Instant.now());
            model.setLanguage(language);
            model.setVoice(voice);
            model.setText(text);
            model.setStatus(TextToSpeechStatus.COMPLETED);

            TextToSpeech entity = model.toEntity();

            entity.setAudioWavBytes(responseBytes);

            entity = repository.save(entity);

            return TextToSpeechModel.fromEntity(entity);
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public Long createSpeechAsync(String language, String voice, String text) {
        try {
            var model = new TextToSpeechModel();
            model.setMoment(Instant.now());
            model.setLanguage(language);
            model.setVoice(voice);
            model.setText(text);
            model.setStatus(TextToSpeechStatus.PROCESSING);

            TextToSpeech entity = repository.save(model.toEntity());

            producer.send(entity.getId());

            return entity.getId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    public TextToSpeechModel search(Long id) throws RecordNotFoundException {
        var entity = findById(id);
        return TextToSpeechModel.fromEntity(entity);
    }

    @Transactional(readOnly = true)
    public byte[] searchAudio(Long id) throws RecordNotFoundException {
        var entity = findById(id);
        return entity.getAudioWavBytes();
    }

    @Transactional(readOnly = true)
    public TextToSpeech findById(Long id) throws RecordNotFoundException {
        return repository.findById(id).orElseThrow(() -> new RecordNotFoundException("Text to speech not found"));
    }

    @Transactional(readOnly = true)
    public Page<TextToSpeechModel> findAllByStatus(TextToSpeechStatus status, Pageable pageable) {
        // Poderia ser implementada uma única query com JPQL
        if (isNull(status)) return repository.findAll(pageable).map(TextToSpeechModel::fromEntity);
        return repository.findAllByStatus(status, pageable).map(TextToSpeechModel::fromEntity);
    }

    public static String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
