import { AsyncPipe } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {
  faArrowsRotate,
  faBackwardStep,
  faExpand,
  faForwardStep,
  faList,
  faPause,
  faPlay,
  faPlus,
  faRepeat,
  faRotateRight,
  faShuffle,
  faVolumeHigh,
  faWindowRestore
} from '@fortawesome/free-solid-svg-icons';
import { environment } from '../../../environments/environment';
import { Track } from '../../core/models/music.models';
import { PlaylistModalService } from '../../core/services/playlist-modal.service';
import { LoopMode, PlayerService } from '../../core/services/player.service';

@Component({
  selector: 'app-audio-player',
  standalone: true,
  imports: [AsyncPipe, FormsModule, FontAwesomeModule, RouterLink],
  templateUrl: './audio-player.component.html',
  styleUrls: ['./audio-player.component.scss']
})
export class AudioPlayerComponent {
  readonly icons = {
    add: faPlus,
    device: faWindowRestore,
    expand: faExpand,
    loopOff: faRepeat,
    loopQueue: faArrowsRotate,
    loopTrack: faRotateRight,
    next: faForwardStep,
    pause: faPause,
    play: faPlay,
    previous: faBackwardStep,
    queue: faList,
    shuffle: faShuffle,
    volume: faVolumeHigh
  };
  readonly track$ = this.player.currentTrack$;
  readonly playing$ = this.player.playing$;
  readonly progress$ = this.player.progress$;
  readonly currentTime$ = this.player.currentTime$;
  readonly duration$ = this.player.duration$;
  readonly volume$ = this.player.volume$;
  readonly loopMode$ = this.player.loopMode$;
  readonly shuffle$ = this.player.shuffle$;

  constructor(readonly player: PlayerService, private readonly playlistModal: PlaylistModalService) {}

  seek(event: Event): void {
    this.player.seek(Number((event.target as HTMLInputElement).value));
  }

  setVolume(event: Event): void {
    this.player.setVolume(Number((event.target as HTMLInputElement).value));
  }

  loopLabel(mode: LoopMode | null): string {
    if (mode === 'track') return 'Loop current track';
    if (mode === 'queue') return 'Loop playlist';

    return 'Loop off';
  }

  loopIcon(mode: LoopMode | null) {
    if (mode === 'track') return this.icons.loopTrack;
    if (mode === 'queue') return this.icons.loopQueue;
    return this.icons.loopOff;
  }

  addToPlaylist(track: Track): void {
    this.playlistModal.open(track);
  }

  coverUrl(track: Track): string | null {
    if (!track.coverS3Key) return null;
    const path = `/api/tracks/${track.id}/cover`;
    return environment.apiBaseUrl ? `${environment.apiBaseUrl}${path}` : path;
  }

  formatTime(seconds: number | null): string {
    const value = Number(seconds ?? 0);
    const safeSeconds = Number.isFinite(value) ? Math.max(0, Math.floor(value)) : 0;
    const minutes = Math.floor(safeSeconds / 60);
    const remainder = safeSeconds % 60;

    return `${minutes}:${remainder.toString().padStart(2, '0')}`;
  }
}
