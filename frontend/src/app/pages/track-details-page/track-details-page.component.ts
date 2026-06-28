import { AsyncPipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faHeart, faPaperPlane, faPlay } from '@fortawesome/free-solid-svg-icons';
import { environment } from '../../../environments/environment';
import { Track, TrackComment, User } from '../../core/models/music.models';
import { AuthService } from '../../core/services/auth.service';
import { NotificationService } from '../../core/services/notification.service';
import { PlayerService } from '../../core/services/player.service';
import { TrackService } from '../../core/services/track.service';

@Component({
  selector: 'app-track-details-page',
  standalone: true,
  imports: [AsyncPipe, FontAwesomeModule, ReactiveFormsModule, RouterLink],
  templateUrl: './track-details-page.component.html',
  styleUrls: ['./track-details-page.component.scss']
})
export class TrackDetailsPageComponent implements OnInit {
  readonly icons = {
    comment: faPaperPlane,
    like: faHeart,
    play: faPlay
  };
  readonly user$ = this.auth.user$;
  readonly commentForm = this.fb.nonNullable.group({
    body: ['', [Validators.required, Validators.maxLength(1200)]]
  });
  track?: Track;
  comments: TrackComment[] = [];
  loading = true;
  commentSaving = false;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly tracks: TrackService,
    private readonly player: PlayerService,
    private readonly auth: AuthService,
    private readonly fb: FormBuilder,
    private readonly notifications: NotificationService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id') ?? '';
    this.tracks.get(id).subscribe({
      next: (track) => {
        this.track = track;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
    this.loadComments(id);
  }

  play(): void {
    if (this.track) this.player.playTrack(this.track, [this.track]);
  }

  toggleLike(): void {
    if (!this.track) return;
    this.tracks.toggleLike(this.track).subscribe((updated) => {
      this.track = updated;
    });
  }

  submitComment(): void {
    if (!this.track) return;
    if (!this.auth.isAuthenticated()) {
      this.notifications.info('Login required', 'Log in to comment on tracks.');
      return;
    }
    if (this.commentForm.invalid) {
      this.notifications.error('Comment required', 'Write a comment before posting.');
      return;
    }

    this.commentSaving = true;
    const body = this.commentForm.getRawValue().body;
    this.tracks.addComment(this.track.id, body).subscribe({
      next: (comment) => {
        this.comments = [comment, ...this.comments];
        this.commentForm.reset({ body: '' });
        this.commentSaving = false;
      },
      error: () => {
        this.commentSaving = false;
      }
    });
  }

  coverUrl(track: Track): string | null {
    if (!track.coverS3Key) return null;
    return `${environment.apiBaseUrl}/api/tracks/${track.id}/cover`;
  }

  avatarUrl(user: User): string | null {
    if (!user.avatarUrl) return null;
    return user.avatarUrl.startsWith('http') ? user.avatarUrl : `${environment.apiBaseUrl}${user.avatarUrl}`;
  }

  formatDate(value: string): string {
    return new Intl.DateTimeFormat('en', { month: 'short', day: 'numeric', year: 'numeric' }).format(new Date(value));
  }

  private loadComments(id: string): void {
    this.tracks.comments(id).subscribe((comments) => {
      this.comments = comments;
    });
  }
}
