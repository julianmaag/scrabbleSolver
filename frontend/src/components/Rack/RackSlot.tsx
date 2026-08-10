import { Tile } from '@/components/Tile/Tile';

type RackSlotProps = {
  letter: string;
  onRemove: () => void;
};

/** A rack tile the player can click to take back. */
export function RackSlot({ letter, onRemove }: RackSlotProps) {
  return (
    <button
      type="button"
      onClick={onRemove}
      aria-label={`Remove ${letter.toUpperCase()}`}
      className="group relative cursor-pointer transition-transform hover:-translate-y-1 active:translate-y-0"
    >
      <Tile letter={letter} size="lg" />
      <span
        aria-hidden
        className="absolute -top-1 -right-1 flex h-4 w-4 items-center justify-center rounded-full bg-letter-rare text-[10px] font-bold leading-none text-white opacity-0 transition-opacity group-hover:opacity-100"
      >
        ×
      </span>
    </button>
  );
}
