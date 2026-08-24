package com.example.scrabblesolver.model;

import com.example.scrabblesolver.model.tiles.Tile;

import java.util.List;

public record Move(Tile[][] board, Word word, int points, List<Placement> placements) implements Comparable<Move> {

    @Override
    public int compareTo(Move o) {
        return o.points - this.points;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Word: ").append(word.getName()).append(", Points: ").append(points).append("\n");
        for (Tile[] tiles : board) {
            for (int j = 0; j < board[0].length; j++) {
                sb.append(tiles[j].toString()).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
