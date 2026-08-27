import { cn } from '@/lib/utils';
import { BLANK } from '@/types/validChar';
import { getLetterValue } from '@/utils/letterValues';

const SIZES = {
  /** Fills its container — used for board squares, where the grid sets the size. */
  xs: { face: 'w-full h-full rounded-none border-0 text-[11px]', value: 'hidden' },
  sm: { face: 'w-8 h-8 text-[13px]', value: 'text-[7px]' },
  md: { face: 'w-11 h-11 text-lg', value: 'text-[8px]' },
  lg: { face: 'w-13 h-13 text-xl', value: 'text-[9px]' },
} as const;

export type TileSize = keyof typeof SIZES;

type TileProps = {
  letter: string;
  size?: TileSize;
  /** A blank tile scores nothing, whichever letter it stands for. Defaults to the `?` face. */
  blank?: boolean;
  className?: string;
};

/** Purely presentational: one flat tile with its letter and, where it fits, its point value. */
export function Tile({ letter, size = 'md', blank = letter === BLANK, className }: TileProps) {
  const value = blank ? 0 : getLetterValue(letter);

  return (
    <div
      className={cn(
        'relative flex shrink-0 select-none items-center justify-center rounded-sm border',
        blank ? 'border-dashed border-ink-faint bg-tile-blank' : 'border-line-strong bg-surface',
        SIZES[size].face,
        className,
      )}
    >
      <span className="font-mono font-semibold leading-none tracking-tight text-ink">
        {letter.toUpperCase()}
      </span>
      {!blank && (
        <span
          className={cn(
            'absolute right-0.5 bottom-0 font-mono leading-none text-ink-muted',
            SIZES[size].value,
          )}
        >
          {value}
        </span>
      )}
    </div>
  );
}
