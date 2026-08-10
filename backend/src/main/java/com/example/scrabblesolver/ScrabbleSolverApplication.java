package com.example.scrabblesolver;

import com.example.scrabblesolver.model.Solutions;
import com.example.scrabblesolver.repository.CommonScrabbleWords;
import com.example.scrabblesolver.repository.TxtFileWordsNWL23;
import com.example.scrabblesolver.service.Simplesolver;
import com.example.scrabblesolver.service.SimplesolverWithJoker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Scanner;

@SpringBootApplication
public class ScrabbleSolverApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScrabbleSolverApplication.class, args);
        SimplesolverWithJoker solver = new SimplesolverWithJoker(new TxtFileWordsNWL23());
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.print("> ");
            String input = scanner.nextLine();
            
            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                System.out.println("Goodbye!");
                break;
            }
            
            Solutions solutions = solver.getSolutions(input);
            System.out.println(solutions);
        }
        scanner.close();
    }

}
