package com.hering.desafiojava.api.controller;

import com.hering.desafiojava.core.enums.TextToSpeechStatus;
import com.hering.desafiojava.core.exception.BadRequestException;
import com.hering.desafiojava.core.exception.RecordNotFoundException;
import com.hering.desafiojava.core.services.TextToSpeechService;
import com.hering.desafiojava.core.services.model.TextToSpeechModel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.map.SingletonMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/text-to-speech")
public class TextToSpeechController {

    private final TextToSpeechService service;

    @GetMapping("{id}")
    public ResponseEntity<TextToSpeechModel> getById(@PathVariable("id") Long id) throws RecordNotFoundException {
        return ResponseEntity.ok(service.search(id));
    }

    @GetMapping("{id}/audio")
    public ResponseEntity<byte[]> getAudioById(@PathVariable("id") Long id) throws RecordNotFoundException {
        byte[] bytes = service.searchAudio(id);
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/wav"));
        return new ResponseEntity<>(bytes, headers, OK);
    }

    @GetMapping("sync")
    public ResponseEntity<TextToSpeechModel> createSync(@RequestParam("text") String text,
                                                        @RequestParam("language") String language,
                                                        @RequestParam("voice") String voice) throws BadRequestException
    {
        return ResponseEntity.ok(service.createSpeech(language, voice, text));
    }

    @GetMapping("async")
    public ResponseEntity<SingletonMap<String, Long>> createAsync(@RequestParam("text") String text, @RequestParam("language") String language,
                                                         @RequestParam("voice") String voice)
    {
        return ResponseEntity.ok(new SingletonMap<>("id", service.createSpeechAsync(language, voice, text)));
    }

    @GetMapping
    public ResponseEntity<Page<TextToSpeechModel>> getAllByStatus(
        @RequestParam(value = "status", required = false) TextToSpeechStatus status, Pageable pageable
    ) {
        return ResponseEntity.ok(service.findAllByStatus(status, pageable));
    }
}
