import { Tile } from '@/components/Tile/Tile';

const LOGO_LETTERS = [...'SCRABBLE'];
const FAN_DEGREES = 1.2;

export function Header() {
  const center = (LOGO_LETTERS.length - 1) / 2;

  return (
    <header className="px-6 pt-10 pb-6 text-center">
      <h1 className="mb-3 flex items-center justify-center gap-1.5">
        <span className="sr-only">Scrabble Rack Solver</span>
        {LOGO_LETTERS.map((letter, index) => (
          <span
            key={index}
            aria-hidden
            style={{ transform: `rotate(${(index - center) * FAN_DEGREES}deg)` }}
          >
            <Tile letter={letter} />
          </span>
        ))}
      </h1>
      <p className="font-display text-[13px] uppercase tracking-[0.18em] text-ink-soft">
        Rack Solver
      </p>
    </header>
  );
}
