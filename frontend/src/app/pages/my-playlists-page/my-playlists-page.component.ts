import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Playlist } from '../../core/models/music.models';
import { PlaylistService } from '../../core/services/playlist.service';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';
import { PlaylistCardComponent } from '../../shared/playlist-card/playlist-card.component';

@Component({
  selector: 'app-my-playlists-page',
  standalone: true,
  imports: [ReactiveFormsModule, PlaylistCardComponent, EmptyStateComponent],
  templateUrl: './my-playlists-page.component.html',
  styleUrls: ['./my-playlists-page.component.scss']
})
export class MyPlaylistsPageComponent implements OnInit {
  playlists: Playlist[] = [];
  readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    description: [''],
    isPublic: [false]
  });

  constructor(private readonly fb: FormBuilder, private readonly playlistService: PlaylistService) {}

  ngOnInit(): void {
    this.load();
  }

  create(): void {
    if (this.form.invalid) return;
    this.playlistService.create(this.form.getRawValue()).subscribe(() => {
      this.form.reset({ name: '', description: '', isPublic: false });
      this.load();
    });
  }

  private load(): void {
    this.playlistService.mine().subscribe((playlists) => {
      this.playlists = playlists;
    });
  }
}
