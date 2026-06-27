import { Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';
import { Album, Artist, Playlist, Track } from '../../core/models/music.models';
import { AlbumService } from '../../core/services/album.service';
import { ArtistService } from '../../core/services/artist.service';
import { PlayerService } from '../../core/services/player.service';
import { PlaylistService } from '../../core/services/playlist.service';
import { TrackService } from '../../core/services/track.service';
import { AlbumCardComponent } from '../../shared/album-card/album-card.component';
import { ArtistCardComponent } from '../../shared/artist-card/artist-card.component';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';
import { LoadingSpinnerComponent } from '../../shared/loading-spinner/loading-spinner.component';
import { MusicCardComponent } from '../../shared/music-card/music-card.component';
import { PlaylistCardComponent } from '../../shared/playlist-card/playlist-card.component';

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [MusicCardComponent, AlbumCardComponent, ArtistCardComponent, PlaylistCardComponent, LoadingSpinnerComponent, EmptyStateComponent],
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.scss']
})
export class HomePageComponent implements OnInit {
  tracks: Track[] = [];
  albums: Album[] = [];
  artists: Artist[] = [];
  playlists: Playlist[] = [];
  loading = true;

  constructor(
    private readonly trackService: TrackService,
    private readonly albumService: AlbumService,
    private readonly artistService: ArtistService,
    private readonly playlistService: PlaylistService,
    private readonly player: PlayerService
  ) {}

  ngOnInit(): void {
    forkJoin({
      tracks: this.trackService.list(12),
      albums: this.albumService.list(8),
      artists: this.artistService.list(8),
      playlists: this.playlistService.publicPlaylists()
    }).subscribe({
      next: ({ tracks, albums, artists, playlists }) => {
        this.tracks = tracks.content;
        this.albums = albums.content;
        this.artists = artists.content;
        this.playlists = playlists;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  play(track: Track): void {
    this.player.playTrack(track, this.tracks);
  }
}
