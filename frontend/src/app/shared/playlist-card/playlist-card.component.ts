import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Playlist } from '../../core/models/music.models';

@Component({
  selector: 'app-playlist-card',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './playlist-card.component.html',
  styleUrls: ['./playlist-card.component.scss']
})
export class PlaylistCardComponent {
  @Input({ required: true }) playlist!: Playlist;
}
