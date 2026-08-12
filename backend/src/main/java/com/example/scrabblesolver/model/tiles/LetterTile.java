package com.example.scrabblesolver.model.tiles;

public record LetterTile(char letter, boolean joker) implements Tile {
	public LetterTile(char letter) {
		this(letter, false);
	}

	@Override
	public String toString(){
		if(joker){
			return "?";
		} else {
			return ""+letter;
		}
	}
}
