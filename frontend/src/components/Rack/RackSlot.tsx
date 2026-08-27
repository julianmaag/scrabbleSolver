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
      className="cursor-pointer transition-transform hover:-translate-y-0.5 active:translate-y-0"
    >
      <Tile letter={letter} size="lg" />
    </button>
  );
}
