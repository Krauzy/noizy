import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Track } from '../models/music.models';

export interface TrackUploadPayload {
  title: string;
  artistId: string;
  albumId?: string;
  genre?: string;
  durationSeconds: number;
  audio: File;
  cover?: File;
}

@Injectable({ providedIn: 'root' })
export class UploadService {
  constructor(private readonly http: HttpClient) {}

  upload(payload: TrackUploadPayload) {
    const form = new FormData();
    form.append('title', payload.title);
    form.append('artistId', payload.artistId);
    if (payload.albumId) form.append('albumId', payload.albumId);
    if (payload.genre) form.append('genre', payload.genre);
    form.append('durationSeconds', String(payload.durationSeconds || 0));
    form.append('audio', payload.audio);
    if (payload.cover) form.append('cover', payload.cover);
    return this.http.post<{ track: Track; audioS3Key: string; coverS3Key?: string }>(`${environment.apiUrl}/tracks/upload`, form);
  }
}
