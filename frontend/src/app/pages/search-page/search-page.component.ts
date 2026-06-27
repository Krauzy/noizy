import { Component } from '@angular/core';
import { Track } from '../../core/models/music.models';
import { PlayerService } from '../../core/services/player.service';
import { TrackService } from '../../core/services/track.service';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';
import { SearchBarComponent } from '../../shared/search-bar/search-bar.component';
import { TrackListComponent } from '../../shared/track-list/track-list.component';

@Component({
  selector: 'app-search-page',
  standalone: true,
  imports: [SearchBarComponent, TrackListComponent, EmptyStateComponent],
  templateUrl: './search-page.component.html',
  styleUrls: ['./search-page.component.scss']
})
export class SearchPageComponent {
  tracks: Track[] = [];
  searched = false;

  constructor(private readonly trackService: TrackService, private readonly player: PlayerService) {}

  search(query: string): void {
    const trimmed = query.trim();
    if (!trimmed) {
      this.tracks = [];
      this.searched = false;
      return;
    }
    this.trackService.search(trimmed).subscribe((page) => {
      this.tracks = page.content;
      this.searched = true;
    });
  }

  play(track: Track): void {
    this.player.playTrack(track, this.tracks);
  }

  like(track: Track): void {
    this.trackService.like(track.id).subscribe();
  }
}
