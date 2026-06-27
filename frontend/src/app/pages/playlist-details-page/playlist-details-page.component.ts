import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Playlist, Track } from '../../core/models/music.models';
import { PlayerService } from '../../core/services/player.service';
import { PlaylistService } from '../../core/services/playlist.service';
import { TrackListComponent } from '../../shared/track-list/track-list.component';

@Component({
  selector: 'app-playlist-details-page',
  standalone: true,
  imports: [TrackListComponent],
  templateUrl: './playlist-details-page.component.html',
  styleUrls: ['./playlist-details-page.component.scss']
})
export class PlaylistDetailsPageComponent implements OnInit {
  playlist?: Playlist;

  constructor(private readonly route: ActivatedRoute, private readonly playlists: PlaylistService, private readonly player: PlayerService) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id') ?? '';
    this.playlists.get(id).subscribe((playlist) => {
      this.playlist = playlist;
    });
  }

  play(track: Track): void {
    this.player.playTrack(track, this.playlist?.tracks ?? [track]);
  }
}
