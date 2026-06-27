import { Component, OnInit } from '@angular/core';
import { User, PlaybackHistory, Track } from '../../core/models/music.models';
import { AuthService } from '../../core/services/auth.service';
import { PlayerService } from '../../core/services/player.service';
import { TrackService } from '../../core/services/track.service';
import { TrackListComponent } from '../../shared/track-list/track-list.component';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [TrackListComponent],
  templateUrl: './profile-page.component.html',
  styleUrls: ['./profile-page.component.scss']
})
export class ProfilePageComponent implements OnInit {
  user?: User;
  history: PlaybackHistory[] = [];
  historyTracks: Track[] = [];

  constructor(private readonly auth: AuthService, private readonly tracks: TrackService, private readonly player: PlayerService) {}

  ngOnInit(): void {
    this.auth.me().subscribe((user) => {
      this.user = user;
    });
    this.tracks.history().subscribe((history) => {
      this.history = history;
      this.historyTracks = history.map((item) => item.track);
    });
  }

  play(track: Track): void {
    this.player.playTrack(track, this.historyTracks);
  }
}
