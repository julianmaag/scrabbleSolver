package com.example.scrabblesolver.repository;

import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface IAvailableWords {
    Set<String> getAvailableWords();
}
