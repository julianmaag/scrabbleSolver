package com.example.scrabblesolver.dto;

import com.example.scrabblesolver.model.Solutions;

import java.util.List;

import static com.example.scrabblesolver.helper.PointCalculator.calculatePointsForWord;

public record SolutionsResponse(List<WordResponse> words) {
    public static SolutionsResponse from(Solutions solutions) {
        return new SolutionsResponse(
                solutions.getWords().stream()
                        .map(w -> new WordResponse(w.getName(), calculatePointsForWord(w.getName())))
                        .toList()
        );
    }
}
