import { Component, input } from '@angular/core';

@Component({
  selector: 'app-tile',
  imports: [],
  templateUrl: './tile.html',
  styleUrl: './tile.css',
  host: {
    class: 'tile flex items-center justify-center bg-amber-50 text-2xl leading-none text-black',
  },
})
export class Tile {
  readonly letter = input.required<string>();
}
