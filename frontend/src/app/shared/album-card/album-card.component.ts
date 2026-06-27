import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Album } from '../../core/models/music.models';

@Component({
  selector: 'app-album-card',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './album-card.component.html',
  styleUrls: ['./album-card.component.scss']
})
export class AlbumCardComponent {
  @Input({ required: true }) album!: Album;
}
