import { cn } from '@/lib/utils';
import { getLetterValue } from '@/utils/letterValues';

const SIZES = {
  sm: { face: 'w-8 h-8 text-sm', value: 'text-[7px]' },
  md: { face: 'w-12 h-12 text-xl', value: 'text-[9px]' },
  lg: { face: 'w-14 h-14 text-2xl', value: 'text-[10px]' },
} as const;

export type TileSize = keyof typeof SIZES;

const BLANK = '?';

function accentClass(value: number): string {
  if (value >= 10) return 'text-letter-legendary';
  if (value >= 8) return 'text-letter-rare';
  if (value >= 5) return 'text-letter-uncommon';
  return 'text-ink';
}

type TileProps = {
  letter: string;
  size?: TileSize;
  className?: string;
};

/** Purely presentational: renders one wooden tile with its letter and point value. */
export function Tile({ letter, size = 'md', className }: TileProps) {
  const value = getLetterValue(letter);
  const isBlank = letter === BLANK;
  const accent = accentClass(value);

  return (
    <div className={cn('relative shrink-0 select-none', SIZES[size].face, className)}>
      <div className="absolute inset-0 translate-y-1 rounded-[3px] bg-tile-shadow" />
      <div
        className={cn(
          'absolute inset-0 flex items-center justify-center rounded-[3px]',
          'border border-tile-edge inset-shadow-tile',
          isBlank
            ? 'bg-tile-blank'
            : 'bg-linear-145 from-tile-light from-0% via-tile via-60% to-tile-dark',
        )}
      >
        <span className={cn('font-mono font-semibold leading-none tracking-tight', accent)}>
          {letter.toUpperCase()}
        </span>
        {!isBlank && (
          <span
            className={cn(
              'absolute right-1 bottom-0.5 font-mono font-medium leading-none',
              SIZES[size].value,
              accent,
            )}
          >
            {value}
          </span>
        )}
      </div>
    </div>
  );
}

