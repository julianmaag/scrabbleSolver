package com.example.scrabblesolver.service;

import com.example.scrabblesolver.model.Solutions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
@Service
public interface ISolverService {
    public Solutions getSolutions(String chars);
}
