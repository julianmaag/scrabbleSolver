package com.example.scrabblesolver;

import com.example.scrabblesolver.helper.DefaultScrabbleBoard;
import com.example.scrabblesolver.model.Move;
import com.example.scrabblesolver.model.tiles.Tile;
import com.example.scrabblesolver.repository.CommonScrabbleWords;
import com.example.scrabblesolver.repository.TxtFileWordsNWL23;
import com.example.scrabblesolver.service.BoardSolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class ScrabbleSolverApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScrabbleSolverApplication.class, args);
        BoardSolver solver = new BoardSolver(new TxtFileWordsNWL23() {
        });
        Scanner scanner = new Scanner(System.in);
        Tile[][] playingBoard = DefaultScrabbleBoard.BOARD;

        while(true){
            System.out.print("> ");
            String input = scanner.nextLine();
            
            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                System.out.println("Goodbye!");
                break;
            }
            
            List<Move> moves = solver.getSolutions(input, playingBoard);
            System.out.println(moves);
        }
        scanner.close();
    }

}
