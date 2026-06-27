import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Playlist } from '../models/music.models';

@Injectable({ providedIn: 'root' })
export class PlaylistService {
  constructor(private readonly http: HttpClient) {}

  mine() {
    return this.http.get<Playlist[]>(`${environment.apiUrl}/playlists/me`);
  }

  publicPlaylists() {
    return this.http.get<Playlist[]>(`${environment.apiUrl}/playlists/public`);
  }

  get(id: string) {
    return this.http.get<Playlist>(`${environment.apiUrl}/playlists/${id}`);
  }

  create(payload: { name: string; description?: string; isPublic: boolean }) {
    return this.http.post<Playlist>(`${environment.apiUrl}/playlists`, payload);
  }

  update(id: string, payload: { name: string; description?: string; isPublic: boolean }) {
    return this.http.put<Playlist>(`${environment.apiUrl}/playlists/${id}`, payload);
  }

  addTrack(playlistId: string, trackId: string) {
    return this.http.post<Playlist>(`${environment.apiUrl}/playlists/${playlistId}/tracks/${trackId}`, {});
  }

  removeTrack(playlistId: string, trackId: string) {
    return this.http.delete<Playlist>(`${environment.apiUrl}/playlists/${playlistId}/tracks/${trackId}`);
  }
}
