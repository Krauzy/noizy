import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faHeart, faPlay } from '@fortawesome/free-solid-svg-icons';
import { environment } from '../../../environments/environment';
import { Track } from '../../core/models/music.models';

@Component({
  selector: 'app-music-card',
  standalone: true,
  imports: [FontAwesomeModule],
  templateUrl: './music-card.component.html',
  styleUrls: ['./music-card.component.scss']
})
export class MusicCardComponent {
  readonly icons = {
    like: faHeart,
    play: faPlay
  };
  @Input({ required: true }) track!: Track;
  @Output() play = new EventEmitter<Track>();
  @Output() like = new EventEmitter<Track>();

  coverUrl(track: Track): string | null {
    if (!track.coverS3Key) return null;
    const path = `/api/tracks/${track.id}/cover`;
    return environment.apiBaseUrl ? `${environment.apiBaseUrl}${path}` : path;
  }
}
