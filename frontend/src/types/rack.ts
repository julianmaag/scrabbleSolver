import { z } from 'zod';
import { validCharSchema } from './validChar';

/** A Scrabble rack holds at most seven tiles. */
export const RACK_SIZE = 7;

export const rackSchema = z.array(validCharSchema).max(RACK_SIZE);

export type Rack = z.infer<typeof rackSchema>;
