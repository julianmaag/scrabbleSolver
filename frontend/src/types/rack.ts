import type { ValidChar } from './validChar';

/** A Scrabble rack holds at most seven tiles. */
export const RACK_SIZE = 7;

export type Rack = readonly ValidChar[];
