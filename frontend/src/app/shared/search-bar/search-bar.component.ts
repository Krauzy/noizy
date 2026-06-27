import { Component, EventEmitter, Output } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
  selector: 'app-search-bar',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.scss']
})
export class SearchBarComponent {
  @Output() search = new EventEmitter<string>();
  readonly query = new FormControl('', { nonNullable: true });

  constructor() {
    this.query.valueChanges.pipe(debounceTime(250), distinctUntilChanged())
      .subscribe((value) => this.search.emit(value));
  }
}
