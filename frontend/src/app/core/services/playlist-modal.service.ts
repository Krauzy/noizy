import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { Track } from '../models/music.models';
import { AuthService } from './auth.service';
import { NotificationService } from './notification.service';

@Injectable({ providedIn: 'root' })
export class PlaylistModalService {
  private readonly trackSubject = new BehaviorSubject<Track | null>(null);
  readonly track$ = this.trackSubject.asObservable();

  constructor(
    private readonly auth: AuthService,
    private readonly router: Router,
    private readonly notifications: NotificationService
  ) {}

  open(track: Track): void {
    if (!this.auth.isAuthenticated()) {
      this.notifications.info('Login required', 'Log in to manage playlists.');
      this.router.navigateByUrl('/login');
      return;
    }
    this.trackSubject.next(track);
  }

  close(): void {
    this.trackSubject.next(null);
  }
}
