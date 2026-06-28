import { Component, OnInit } from '@angular/core';
import { HttpEventType } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Album } from '../../core/models/music.models';
import { AlbumService } from '../../core/services/album.service';
import { NotificationService } from '../../core/services/notification.service';
import { UploadService } from '../../core/services/upload.service';

@Component({
  selector: 'app-upload-track-page',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './upload-track-page.component.html',
  styleUrls: ['./upload-track-page.component.scss']
})
export class UploadTrackPageComponent implements OnInit {
  albums: Album[] = [];
  audioFile?: File;
  coverFile?: File;
  coverPreviewUrl = '';
  message = '';
  error = '';
  uploading = false;
  uploadProgress = 0;
  readonly form = this.fb.nonNullable.group({
    title: ['', Validators.required],
    albumId: [''],
    genre: ['']
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly albumsService: AlbumService,
    private readonly uploadService: UploadService,
    private readonly notifications: NotificationService
  ) {}

  ngOnInit(): void {
    this.albumsService.list(100).subscribe((page) => {
      this.albums = page.content;
    });
  }

  fileSelected(event: Event, kind: 'audio' | 'cover'): void {
    const file = (event.target as HTMLInputElement).files?.item(0) ?? undefined;
    this.error = '';

    if (!file) {
      if (kind === 'audio') {
        this.audioFile = undefined;
      } else {
        this.clearCoverPreview();
        this.coverFile = undefined;
      }
      return;
    }

    if (kind === 'audio') {
      if (!this.isValidAudio(file)) {
        this.audioFile = undefined;
        this.error = 'Use an MP3, WAV, OGG, AAC, M4A or FLAC audio file.';
        this.notifications.error('Invalid audio', this.error);
        return;
      }
      this.audioFile = file;
    } else {
      if (!file.type.startsWith('image/')) {
        this.coverFile = undefined;
        this.clearCoverPreview();
        this.error = 'Use an image file for the cover.';
        this.notifications.error('Invalid cover', this.error);
        return;
      }
      this.clearCoverPreview();
      this.coverFile = file;
      this.coverPreviewUrl = URL.createObjectURL(file);
    }
  }

  submit(): void {
    if (this.form.invalid || !this.audioFile) {
      this.error = 'Select a title and audio file.';
      return;
    }
    this.error = '';
    this.message = '';
    this.uploading = true;
    this.uploadProgress = 0;
    const value = this.form.getRawValue();
    this.uploadService.upload({
      title: value.title,
      albumId: value.albumId || undefined,
      genre: value.genre || undefined,
      audio: this.audioFile,
      cover: this.coverFile
    }).subscribe({
      next: (event) => {
        if (event.type === HttpEventType.UploadProgress) {
          this.uploadProgress = event.total ? Math.round((event.loaded / event.total) * 100) : 0;
          return;
        }

        if (event.type === HttpEventType.Response && event.body) {
          this.uploadProgress = 100;
          this.message = `${event.body.track.title} uploaded`;
          this.notifications.success('Upload complete', this.message);
          this.form.reset({ title: '', albumId: '', genre: '' });
          this.audioFile = undefined;
          this.coverFile = undefined;
          this.clearCoverPreview();
          this.uploading = false;
        }
      },
      error: () => {
        this.uploading = false;
        this.uploadProgress = 0;
        this.error = 'Upload failed. Check LocalStack and backend logs.';
      }
    });
  }

  private isValidAudio(file: File): boolean {
    const allowedTypes = ['audio/mpeg', 'audio/wav', 'audio/x-wav', 'audio/ogg', 'audio/aac', 'audio/mp4', 'audio/flac'];
    const allowedExtensions = ['mp3', 'wav', 'ogg', 'aac', 'm4a', 'flac'];
    const extension = file.name.split('.').pop()?.toLowerCase() ?? '';
    return allowedTypes.includes(file.type) || allowedExtensions.includes(extension);
  }

  private clearCoverPreview(): void {
    if (this.coverPreviewUrl) {
      URL.revokeObjectURL(this.coverPreviewUrl);
      this.coverPreviewUrl = '';
    }
  }
}
