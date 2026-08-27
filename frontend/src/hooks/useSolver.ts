import { useCallback, useEffect, useRef, useState } from 'react';
import { solve } from '@/services/solverService';
import type { BoardState } from '@/types/board';
import type { Move } from '@/types/move';
import type { Rack } from '@/types/rack';

/** A discriminated union makes impossible states (e.g. loading *and* solved) unrepresentable. */
export type SolverState =
  | { status: 'idle' }
  | { status: 'loading' }
  | { status: 'solved'; moves: Move[] }
  | { status: 'error'; message: string };

export function useSolver() {
  const [state, setState] = useState<SolverState>({ status: 'idle' });
  const inFlight = useRef<AbortController>(null);

  // Abandoning a request is normal here, so never surface it as an error.
  const abort = useCallback(() => {
    inFlight.current?.abort();
    inFlight.current = null;
  }, []);

  useEffect(() => abort, [abort]);

  const run = useCallback(
    async (rack: Rack, board: BoardState) => {
      abort();
      const controller = new AbortController();
      inFlight.current = controller;
      setState({ status: 'loading' });

      try {
        const moves = await solve(rack, board, controller.signal);
        setState({ status: 'solved', moves });
      } catch (error) {
        if (controller.signal.aborted) return;
        setState({
          status: 'error',
          message: error instanceof Error ? error.message : 'Could not reach the solver.',
        });
      }
    },
    [abort],
  );

  const reset = useCallback(() => {
    abort();
    setState({ status: 'idle' });
  }, [abort]);

  return { state, solve: run, reset };
}
