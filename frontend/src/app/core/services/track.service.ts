import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { map } from 'rxjs';
import { Page, PlaybackHistory, Track, TrackComment } from '../models/music.models';

@Injectable({ providedIn: 'root' })
export class TrackService {
  constructor(private readonly http: HttpClient) {}

  list(size = 40) {
    return this.http.get<Page<Track>>(`${environment.apiUrl}/tracks`, { params: { size } });
  }

  search(query: string, size = 40) {
    return this.http.get<Page<Track>>(`${environment.apiUrl}/tracks/search`, { params: { query, size } });
  }

  get(id: string) {
    return this.http.get<Track>(`${environment.apiUrl}/tracks/${id}`);
  }

  liked() {
    return this.http.get<Track[]>(`${environment.apiUrl}/tracks/liked`);
  }

  like(id: string) {
    return this.http.post<Track>(`${environment.apiUrl}/tracks/${id}/like`, {});
  }

  unlike(id: string) {
    return this.http.delete<Track>(`${environment.apiUrl}/tracks/${id}/like`);
  }

  toggleLike(track: Track) {
    return track.liked
      ? this.unlike(track.id)
      : this.like(track.id);
  }

  recordPlayback(id: string) {
    return this.http.post<PlaybackHistory>(`${environment.apiUrl}/playback/${id}`, {});
  }

  history() {
    return this.http.get<PlaybackHistory[]>(`${environment.apiUrl}/playback/history`);
  }

  comments(id: string) {
    return this.http.get<TrackComment[]>(`${environment.apiUrl}/tracks/${id}/comments`);
  }

  addComment(id: string, body: string) {
    return this.http.post<TrackComment>(`${environment.apiUrl}/tracks/${id}/comments`, { body }).pipe(
      map((comment) => comment)
    );
  }
}
