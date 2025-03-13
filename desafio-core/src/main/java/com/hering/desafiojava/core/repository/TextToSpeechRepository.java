package com.hering.desafiojava.core.repository;

import com.hering.desafiojava.core.entities.TextToSpeech;
import com.hering.desafiojava.core.enums.TextToSpeechStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TextToSpeechRepository extends JpaRepository<TextToSpeech, Long> {

    Page<TextToSpeech> findAllByStatus(TextToSpeechStatus status, Pageable pageable);
}