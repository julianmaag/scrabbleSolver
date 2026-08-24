package com.example.scrabblesolver.dto;

import java.util.List;

/**
 * Json could look like this
 * [
 *   {
 *     "word": "QUARTZ",
 *     "points": 46,
 *     "row": 4,
 *     "column": 8,
 *     "direction": "DOWN",
 *     "placements": [
 *       { "row": 4, "column": 8, "rackTile": "Q", "playedAs": "Q", "blank": false },
 *       { "row": 5, "column": 8, "rackTile": "?", "playedAs": "U", "blank": true  },
 *       { "row": 7, "column": 8, "rackTile": "R", "playedAs": "R", "blank": false },
 *       { "row": 8, "column": 8, "rackTile": "T", "playedAs": "T", "blank": false },
 *       { "row": 9, "column": 8, "rackTile": "Z", "playedAs": "Z", "blank": false }
 *     ]
 *   },
 *   {
 *     "word": "QUIZ",
 *     "points": 33,
 *     "row": 4,
 *     "column": 8,
 *     "direction": "DOWN",
 *     "placements": [ ... ]
 *   }
 * ]
 *
 * @param moves
 */
public record SolveResponse(List<MoveDto> moves) {}

