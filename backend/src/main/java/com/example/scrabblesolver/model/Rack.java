package com.example.scrabblesolver.model;

public final class Rack {
    private static final int BLANK = 26;
    public  static final char NONE = 0;
    private final int[] counts;
    private int remaining;

    private Rack(int[] counts, int remaining) {
        this.counts = counts;
        this.remaining = remaining;
    }

    public static Rack of(String tiles) {
        int[] counts = new int[27];
        int n = 0;
        for (char c : tiles.toUpperCase().toCharArray()) {
            counts[idx(c)]++;
            n++;
        }
        return new Rack(counts, n);
    }

    private static int idx(char c) {
        return c == '?' ? BLANK : Character.toUpperCase(c) - 'A';
    }

    /**
     * Consumes a letter from the rack
     * @param letter the letter to consume
     * @return 0 if not in rack, else the char and ? if joker
     */
    public char consume(char letter) {
        int i = idx(letter);
        if (counts[i] > 0)     { counts[i]--;     remaining--; return letter; }
        if (counts[BLANK] > 0) { counts[BLANK]--; remaining--; return '?';    }
        return NONE;
    }

    public int remaining() { return remaining; }

    public Rack copy() { return new Rack(counts.clone(), remaining); }
}