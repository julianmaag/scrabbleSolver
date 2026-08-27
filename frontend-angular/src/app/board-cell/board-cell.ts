import { Component, computed, input, output } from '@angular/core';
import { Tile } from '../tile/tile';
import { CellType } from '@/shared/cell-type';

@Component({
  selector: 'app-board-cell',
  imports: [Tile],
  templateUrl: './board-cell.html',
  styleUrl: './board-cell.css',
})
export class BoardCell {
readonly letter = input<string>('');
readonly type = input<CellType>(CellType.None);

readonly selected = input(false);
readonly picked = output<void>();
}
