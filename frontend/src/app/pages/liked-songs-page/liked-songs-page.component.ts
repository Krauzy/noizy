import { Component, OnInit } from '@angular/core';
import { Track } from '../../core/models/music.models';
import { PlayerService } from '../../core/services/player.service';
import { TrackService } from '../../core/services/track.service';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';
import { TrackListComponent } from '../../shared/track-list/track-list.component';

@Component({
  selector: 'app-liked-songs-page',
  standalone: true,
  imports: [TrackListComponent, EmptyStateComponent],
  templateUrl: './liked-songs-page.component.html',
  styleUrls: ['./liked-songs-page.component.scss']
})
export class LikedSongsPageComponent implements OnInit {
  tracks: Track[] = [];

  constructor(private readonly trackService: TrackService, private readonly player: PlayerService) {}

  ngOnInit(): void {
    this.load();
  }

  play(track: Track): void {
    this.player.playTrack(track, this.tracks);
  }

  unlike(track: Track): void {
    this.trackService.toggleLike(track).subscribe(() => this.load());
  }

  private load(): void {
    this.trackService.liked().subscribe((tracks) => {
      this.tracks = tracks;
    });
  }
}
