package ttsWorker.consumer;

import com.hering.desafiojava.core.entities.TextToSpeech;
import com.hering.desafiojava.core.exception.BadRequestException;
import com.hering.desafiojava.core.exception.RecordNotFoundException;
import com.hering.desafiojava.core.repository.TextToSpeechRepository;
import com.hering.desafiojava.core.services.TextToSpeechService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ttsWorker.exception.TextToSpeechAlreadyProcessedException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static com.hering.desafiojava.core.enums.TextToSpeechStatus.COMPLETED;
import static com.hering.desafiojava.core.services.TextToSpeechService.LANGUAGE_NOT_SUPPORTED;
import static com.hering.desafiojava.core.services.TextToSpeechService.encodeValue;
import static java.util.Objects.nonNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class TextToSpeechConsumer {

    private final TextToSpeechService service;
    private final TextToSpeechRepository repository;

    private final static HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build();

    @Transactional(rollbackFor = Exception.class)
    @KafkaListener(topics = "tts-requests", groupId = "hering_tts_challenge")
    @RetryableTopic(exclude = { TextToSpeechAlreadyProcessedException.class }, attempts = "5", backoff = @Backoff(delay = 30000, multiplier = 2.0))
    public void consume(String textToSpeechId) throws ConnectException {
        try {
            TextToSpeech textToSpeech = service.findById(Long.valueOf(textToSpeechId));
            if (textToSpeech.getStatus().equals(COMPLETED) || nonNull(textToSpeech.getAudioWavBytes())) {
                throw new TextToSpeechAlreadyProcessedException("Text to speech already processed");
            }

            String url = "http://api.voicerss.org/?key=b52ad1e1a7ea44e799babb04ff4d229f&hl=" + textToSpeech.getLanguage()
                + "&v=" + textToSpeech.getVoice() + "&src=" + encodeValue(textToSpeech.getText());

            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET()
                .build();

            var httpResponse = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());

            byte[] responseBytes = httpResponse.body().readAllBytes();

            String bodyString = new String(responseBytes, StandardCharsets.UTF_8);
            if (httpResponse.statusCode() != 200 || LANGUAGE_NOT_SUPPORTED.equals(bodyString)) {
                throw new BadRequestException(bodyString);
            }
            textToSpeech.setAudioWavBytes(responseBytes);
            textToSpeech.setStatus(COMPLETED);
            repository.save(textToSpeech);
        } catch (
            RecordNotFoundException | IOException | InterruptedException | BadRequestException |
            URISyntaxException | TextToSpeechAlreadyProcessedException e
        ) {
            String suffix = e instanceof ConnectException ? "Internet connection went down." : "";
            log.error("Error while trying to process text to speech. %s".formatted(suffix));
            if (e instanceof ConnectException ce) throw ce;
        }
    }
}
