import { HttpClient } from '@angular/common/http';
import { HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Track } from '../models/music.models';

export interface TrackUploadPayload {
  title: string;
  albumId?: string;
  genre?: string;
  audio: File;
  cover?: File;
}

@Injectable({ providedIn: 'root' })
export class UploadService {
  constructor(private readonly http: HttpClient) {}

  upload(payload: TrackUploadPayload): Observable<HttpEvent<{ track: Track; audioS3Key: string; coverS3Key?: string }>> {
    const form = new FormData();
    form.append('title', payload.title);
    if (payload.albumId) form.append('albumId', payload.albumId);
    if (payload.genre) form.append('genre', payload.genre);
    form.append('audio', payload.audio);
    if (payload.cover) form.append('cover', payload.cover);
    return this.http.post<{ track: Track; audioS3Key: string; coverS3Key?: string }>(
      `${environment.apiUrl}/tracks/upload`,
      form,
      { observe: 'events', reportProgress: true }
    );
  }
}
