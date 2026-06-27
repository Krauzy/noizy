import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Page, PlaybackHistory, Track } from '../models/music.models';

@Injectable({ providedIn: 'root' })
export class TrackService {
  constructor(private readonly http: HttpClient) {}

  list(size = 40) {
    return this.http.get<Page<Track>>(`${environment.apiUrl}/tracks`, { params: { size } });
  }

  search(query: string, size = 40) {
    return this.http.get<Page<Track>>(`${environment.apiUrl}/tracks/search`, { params: { query, size } });
  }

  liked() {
    return this.http.get<Track[]>(`${environment.apiUrl}/tracks/liked`);
  }

  like(id: string) {
    return this.http.post<Track>(`${environment.apiUrl}/tracks/${id}/like`, {});
  }

  unlike(id: string) {
    return this.http.delete<void>(`${environment.apiUrl}/tracks/${id}/like`);
  }

  recordPlayback(id: string) {
    return this.http.post<PlaybackHistory>(`${environment.apiUrl}/playback/${id}`, {});
  }

  history() {
    return this.http.get<PlaybackHistory[]>(`${environment.apiUrl}/playback/history`);
  }
}
