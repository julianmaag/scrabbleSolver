import { useCallback, useState } from 'react';
import { solveRack } from '@/services/solverService';
import type { Rack } from '@/types/rack';
import type { ScoredWord } from '@/types/solution';

/** A discriminated union makes impossible states (e.g. loading *and* solved) unrepresentable. */
export type SolverState =
  | { status: 'idle' }
  | { status: 'loading' }
  | { status: 'solved'; words: ScoredWord[] }
  | { status: 'error'; message: string };

export function useSolver() {
  const [state, setState] = useState<SolverState>({ status: 'idle' });

  const solve = useCallback(async (rack: Rack) => {
    if (rack.length === 0) return;
    setState({ status: 'loading' });
    try {
      setState({ status: 'solved', words: await solveRack(rack) });
    } catch (error) {
      setState({
        status: 'error',
        message: error instanceof Error ? error.message : 'Could not reach the solver.',
      });
    }
  }, []);

  const reset = useCallback(() => setState({ status: 'idle' }), []);

  return { state, solve, reset };
}
