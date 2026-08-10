/** Placeholder for a tile the player has not filled in yet. */
export function EmptySlot() {
  return (
    <div
      aria-hidden
      className="h-14 w-14 shrink-0 rounded-[3px] border-2 border-dashed border-tile-shadow/35 bg-white/15"
    />
  );
}
