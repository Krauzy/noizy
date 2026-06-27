import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Artist } from '../../core/models/music.models';

@Component({
  selector: 'app-artist-card',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './artist-card.component.html',
  styleUrls: ['./artist-card.component.scss']
})
export class ArtistCardComponent {
  @Input({ required: true }) artist!: Artist;
}
