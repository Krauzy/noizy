import { AsyncPipe } from '@angular/common';
import { Component } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faCirclePlay, faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';
import { Router, RouterLink } from '@angular/router';
import { catchError, debounceTime, distinctUntilChanged, map, of, switchMap, tap } from 'rxjs';
import { Track } from '../../core/models/music.models';
import { AuthService } from '../../core/services/auth.service';
import { PlayerService } from '../../core/services/player.service';
import { TrackService } from '../../core/services/track.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [AsyncPipe, FontAwesomeModule, ReactiveFormsModule, RouterLink],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent {
  readonly icons = {
    play: faCirclePlay,
    search: faMagnifyingGlass
  };
  readonly query = new FormControl('', { nonNullable: true });
  readonly user$ = this.auth.user$;
  tracks: Track[] = [];
  searchOpen = false;
  searching = false;

  constructor(
    private readonly auth: AuthService,
    private readonly player: PlayerService,
    private readonly router: Router,
    private readonly trackService: TrackService
  ) {
    this.query.valueChanges.pipe(
      debounceTime(220),
      distinctUntilChanged(),
      tap((query) => {
        const hasQuery = query.trim().length > 0;
        this.searching = hasQuery;
        if (!hasQuery) {
          this.tracks = [];
          this.searching = false;
        }
      }),
      switchMap((query) => {
        const trimmed = query.trim();
        if (!trimmed) return of([]);
        return this.trackService.search(trimmed, 6).pipe(
          map((page) => page.content),
          catchError(() => of([]))
        );
      })
    ).subscribe((tracks) => {
      this.tracks = tracks;
      this.searching = false;
    });
  }

  get hasSearchQuery(): boolean {
    return this.query.value.trim().length > 0;
  }

  openSearch(): void {
    this.searchOpen = true;
  }

  closeSearch(): void {
    this.searchOpen = false;
  }

  play(track: Track): void {
    this.player.playTrack(track, this.tracks.length ? this.tracks : [track]);
    this.query.setValue('', { emitEvent: false });
    this.tracks = [];
    this.closeSearch();
  }

  logout(): void {
    this.auth.logout();
    this.router.navigateByUrl('/login');
  }
}
