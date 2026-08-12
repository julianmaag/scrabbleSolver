package com.example.scrabblesolver.model;

import com.example.scrabblesolver.model.tiles.Tile;

public class Move implements Comparable<Move>{
    private Tile[][] board;
    private Word word;
    private int points;

    public Move(Tile[][] board, Word word, int points) {
        this.board = board;
        this.word = word;
        this.points = points;
    }

    public Tile[][] getBoard() {
        return board;
    }

    public void setBoard(Tile[][] board) {
        this.board = board;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public int compareTo(Move o) {
        return o.points - points;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Word: ").append(word.getName()).append(", Points: ").append(points).append("\n");
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[0].length; j++){
                sb.append(board[i][j].toString()).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
