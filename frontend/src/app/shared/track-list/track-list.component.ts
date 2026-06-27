import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faHeart, faPlay } from '@fortawesome/free-solid-svg-icons';
import { Track } from '../../core/models/music.models';

@Component({
  selector: 'app-track-list',
  standalone: true,
  imports: [FontAwesomeModule],
  templateUrl: './track-list.component.html',
  styleUrls: ['./track-list.component.scss']
})
export class TrackListComponent {
  readonly icons = {
    like: faHeart,
    play: faPlay
  };
  @Input({ required: true }) tracks: Track[] = [];
  @Output() play = new EventEmitter<Track>();
  @Output() like = new EventEmitter<Track>();
}
