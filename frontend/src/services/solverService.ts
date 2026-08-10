import { solutionsSchema, type ScoredWord } from '@/types/solution';
import type { Rack } from '@/types/rack';

const SOLVE_ENDPOINT = '/api/solve';

/** Asks the backend for every word playable from the given rack, best score first. */
export async function solveRack(rack: Rack, signal?: AbortSignal): Promise<ScoredWord[]> {
  const response = await fetch(SOLVE_ENDPOINT, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ letters: rack.join('') }),
    signal,
  });

  if (!response.ok) {
    throw new Error(`Solver request failed with status ${response.status}`);
  }

  // Parse instead of cast: the network is a trust boundary.
  return solutionsSchema.parse(await response.json()).words;
}
