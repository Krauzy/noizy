import { Component, OnInit } from '@angular/core';
import { Playlist } from '../../core/models/music.models';
import { PlaylistService } from '../../core/services/playlist.service';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';
import { PlaylistCardComponent } from '../../shared/playlist-card/playlist-card.component';

@Component({
  selector: 'app-my-playlists-page',
  standalone: true,
  imports: [PlaylistCardComponent, EmptyStateComponent],
  templateUrl: './my-playlists-page.component.html',
  styleUrls: ['./my-playlists-page.component.scss']
})
export class MyPlaylistsPageComponent implements OnInit {
  playlists: Playlist[] = [];

  constructor(private readonly playlistService: PlaylistService) {}

  ngOnInit(): void {
    this.load();
  }

  private load(): void {
    this.playlistService.mine().subscribe((playlists) => {
      this.playlists = playlists;
    });
  }
}
