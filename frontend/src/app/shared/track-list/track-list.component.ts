import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { RouterLink } from '@angular/router';
import { faEllipsis, faHeart, faPlay } from '@fortawesome/free-solid-svg-icons';
import { Track } from '../../core/models/music.models';
import { PlaylistModalService } from '../../core/services/playlist-modal.service';

@Component({
  selector: 'app-track-list',
  standalone: true,
  imports: [FontAwesomeModule, RouterLink],
  templateUrl: './track-list.component.html',
  styleUrls: ['./track-list.component.scss']
})
export class TrackListComponent {
  readonly icons = {
    options: faEllipsis,
    like: faHeart,
    play: faPlay
  };
  @Input({ required: true }) tracks: Track[] = [];
  @Output() play = new EventEmitter<Track>();
  @Output() like = new EventEmitter<Track>();

  constructor(private readonly playlistModal: PlaylistModalService) {}

  openOptions(track: Track): void {
    this.playlistModal.open(track);
  }
}
