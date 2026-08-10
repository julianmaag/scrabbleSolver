package com.example.scrabblesolver.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Primary
@Repository
public class TxtFileWordsNWL23 implements IAvailableWords {
    private static final String RESOURCE_PATH = "static/WordSources/nwl2023-words.txt";

    public TxtFileWordsNWL23() {

    }

    private static final Set<String> WORD_SET = loadWords();

    private static Set<String> loadWords() {
        try (InputStream in = new ClassPathResource(RESOURCE_PATH).getInputStream()) {
            String content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            return Arrays.stream(content.split("\\s+"))
                    .map(String::toLowerCase)
                    .filter(w -> w.length() >= 2)
                    .collect(Collectors.toUnmodifiableSet());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load word list from " + RESOURCE_PATH, e);
        }
    }

    @Override
    public Set<String> getAvailableWords() {
        return WORD_SET;
    }
}
