import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Album, Artist } from '../../core/models/music.models';
import { AlbumService } from '../../core/services/album.service';
import { ArtistService } from '../../core/services/artist.service';
import { UploadService } from '../../core/services/upload.service';

@Component({
  selector: 'app-upload-track-page',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './upload-track-page.component.html',
  styleUrls: ['./upload-track-page.component.scss']
})
export class UploadTrackPageComponent implements OnInit {
  artists: Artist[] = [];
  albums: Album[] = [];
  audioFile?: File;
  coverFile?: File;
  message = '';
  error = '';
  readonly form = this.fb.nonNullable.group({
    title: ['', Validators.required],
    artistId: ['', Validators.required],
    albumId: [''],
    genre: [''],
    durationSeconds: [0]
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly artistsService: ArtistService,
    private readonly albumsService: AlbumService,
    private readonly uploadService: UploadService
  ) {}

  ngOnInit(): void {
    this.artistsService.list(100).subscribe((page) => {
      this.artists = page.content;
    });
    this.albumsService.list(100).subscribe((page) => {
      this.albums = page.content;
    });
  }

  fileSelected(event: Event, kind: 'audio' | 'cover'): void {
    const file = (event.target as HTMLInputElement).files?.item(0) ?? undefined;
    if (kind === 'audio') {
      this.audioFile = file;
    } else {
      this.coverFile = file;
    }
  }

  submit(): void {
    if (this.form.invalid || !this.audioFile) {
      this.error = 'Select a title, artist, and audio file.';
      return;
    }
    this.error = '';
    this.message = '';
    const value = this.form.getRawValue();
    this.uploadService.upload({
      title: value.title,
      artistId: value.artistId,
      albumId: value.albumId || undefined,
      genre: value.genre || undefined,
      durationSeconds: value.durationSeconds || 0,
      audio: this.audioFile,
      cover: this.coverFile
    }).subscribe({
      next: ({ track }) => {
        this.message = `${track.title} uploaded`;
        this.form.reset({ title: '', artistId: '', albumId: '', genre: '', durationSeconds: 0 });
        this.audioFile = undefined;
        this.coverFile = undefined;
      },
      error: () => {
        this.error = 'Upload failed. Check LocalStack and backend logs.';
      }
    });
  }
}
