package com.example.scrabblesolver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.scrabblesolver.repository.LetterValues.getLetterValue;

@RestController
@RequestMapping("/api")
public class PointController {

    public PointController() {

    }

    @GetMapping
    public int pointPerChar(@RequestAttribute Character character){
        return getLetterValue(character);
    }
}
