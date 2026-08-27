import { Component, input, signal } from '@angular/core';
import { BoardCell } from '../board-cell/board-cell';
import { CellType } from '@/shared/cell-type';
import { isLetter, safeParse } from '@/shared/letter';

export interface Cell {
  letter: string;
  type: CellType;
}

function emptyBoard(): Cell[][] {
  const cells = Array.from({ length: 15 }, () =>
    Array.from({ length: 15 }, (): Cell => ({ letter: '', type: CellType.None })),
  );
  cells[7][7].type = CellType.DoubleWord; // Center cell is a double word score
  return cells;
}

@Component({
  selector: 'app-board',
  imports: [BoardCell],
  templateUrl: './board.html',
  styleUrl: './board.css',
})
export class Board {
  readonly board = input<Cell[][]>(emptyBoard());

  place(row: number, col: number, letter: string) {
    let parsedLetter = safeParse(letter);
    this.board()[row][col].letter = parsedLetter;
  }

  remove(row: number, col: number) {
    this.board()[row][col].letter = '';
  }

  readonly selected = signal<{ row: number; col: number } | null>(null);

  select(row: number, col: number) {
    this.selected.set({ row, col });
  }

  selectNext(row: number, col: number) {
    this.selected.set({ row: row + 1, col });
  }

  isSelected(row: number, col: number) {
    const s = this.selected();
    return s?.row === row && s?.col === col;
  }

  handleOnKeydown(event: KeyboardEvent, row: number, col: number) {
    if(isLetter(event.key)){
      this.place(row, col, event.key)
    } else if(event.key === 'Tab'){
      this.selectNext(row, col);
    } else if(event.key === 'Backspace' || event.key === 'Delete'){
      this.remove(row, col);
    }
  }
}
