import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faPlay } from '@fortawesome/free-solid-svg-icons';
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
    play: faPlay
  };
  @Input({ required: true }) track!: Track;
  @Output() play = new EventEmitter<Track>();
}
