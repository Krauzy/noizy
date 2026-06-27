import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Album, Track } from '../../core/models/music.models';
import { AlbumService } from '../../core/services/album.service';
import { PlayerService } from '../../core/services/player.service';
import { TrackListComponent } from '../../shared/track-list/track-list.component';

@Component({
  selector: 'app-album-details-page',
  standalone: true,
  imports: [TrackListComponent],
  templateUrl: './album-details-page.component.html',
  styleUrls: ['./album-details-page.component.scss']
})
export class AlbumDetailsPageComponent implements OnInit {
  album?: Album;
  tracks: Track[] = [];

  constructor(private readonly route: ActivatedRoute, private readonly albums: AlbumService, private readonly player: PlayerService) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id') ?? '';
    forkJoin({ album: this.albums.get(id), tracks: this.albums.tracks(id) })
      .subscribe(({ album, tracks }) => {
        this.album = album;
        this.tracks = tracks;
      });
  }

  play(track: Track): void {
    this.player.playTrack(track, this.tracks);
  }
}
