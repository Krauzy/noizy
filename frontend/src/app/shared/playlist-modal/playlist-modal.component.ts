import { AsyncPipe } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faCirclePlus, faList, faXmark } from '@fortawesome/free-solid-svg-icons';
import { Playlist, Track } from '../../core/models/music.models';
import { NotificationService } from '../../core/services/notification.service';
import { PlaylistModalService } from '../../core/services/playlist-modal.service';
import { PlaylistService } from '../../core/services/playlist.service';

@Component({
  selector: 'app-playlist-modal',
  standalone: true,
  imports: [AsyncPipe, FontAwesomeModule, ReactiveFormsModule],
  templateUrl: './playlist-modal.component.html',
  styleUrls: ['./playlist-modal.component.scss']
})
export class PlaylistModalComponent {
  readonly track$ = this.modal.track$;
  readonly icons = {
    add: faCirclePlus,
    close: faXmark,
    playlist: faList
  };
  readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    description: [''],
    isPublic: [false]
  });
  playlists: Playlist[] = [];
  loading = false;
  creating = false;

  constructor(
    readonly modal: PlaylistModalService,
    private readonly playlistsService: PlaylistService,
    private readonly fb: FormBuilder,
    private readonly notifications: NotificationService
  ) {
    this.track$.subscribe((track) => {
      if (track) this.loadPlaylists();
    });
  }

  addToPlaylist(playlist: Playlist, track: Track): void {
    this.playlistsService.addTrack(playlist.id, track.id).subscribe({
      next: () => {
        this.notifications.success('Playlist updated', `${track.title} added to ${playlist.name}.`);
        this.modal.close();
      }
    });
  }

  createAndAdd(track: Track): void {
    if (this.form.invalid) {
      this.notifications.error('Playlist name required', 'Name the playlist before creating it.');
      return;
    }

    this.creating = true;
    const value = this.form.getRawValue();
    this.playlistsService.create({
      name: value.name,
      description: value.description || undefined,
      isPublic: value.isPublic
    }).subscribe({
      next: (playlist) => {
        this.playlistsService.addTrack(playlist.id, track.id).subscribe({
          next: () => {
            this.creating = false;
            this.form.reset({ name: '', description: '', isPublic: false });
            this.notifications.success('Playlist created', `${track.title} added to ${playlist.name}.`);
            this.modal.close();
          },
          error: () => {
            this.creating = false;
          }
        });
      },
      error: () => {
        this.creating = false;
      }
    });
  }

  private loadPlaylists(): void {
    this.loading = true;
    this.playlistsService.mine().subscribe({
      next: (playlists) => {
        this.playlists = playlists;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }
}
