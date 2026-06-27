import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Album, Artist, Track } from '../../core/models/music.models';
import { AlbumService } from '../../core/services/album.service';
import { ArtistService } from '../../core/services/artist.service';
import { PlayerService } from '../../core/services/player.service';
import { TrackService } from '../../core/services/track.service';
import { AlbumCardComponent } from '../../shared/album-card/album-card.component';
import { TrackListComponent } from '../../shared/track-list/track-list.component';

@Component({
  selector: 'app-artist-details-page',
  standalone: true,
  imports: [AlbumCardComponent, TrackListComponent],
  templateUrl: './artist-details-page.component.html',
  styleUrls: ['./artist-details-page.component.scss']
})
export class ArtistDetailsPageComponent implements OnInit {
  artist?: Artist;
  albums: Album[] = [];
  tracks: Track[] = [];

  constructor(
    private readonly route: ActivatedRoute,
    private readonly artists: ArtistService,
    private readonly albumsService: AlbumService,
    private readonly tracksService: TrackService,
    private readonly player: PlayerService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id') ?? '';
    forkJoin({
      artist: this.artists.get(id),
      albums: this.albumsService.list(80),
      tracks: this.tracksService.list(80)
    }).subscribe(({ artist, albums, tracks }) => {
      this.artist = artist;
      this.albums = albums.content.filter((album) => album.artist.id === artist.id);
      this.tracks = tracks.content.filter((track) => track.artist.id === artist.id);
    });
  }

  play(track: Track): void {
    this.player.playTrack(track, this.tracks);
  }
}
