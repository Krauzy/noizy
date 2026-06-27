import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Album, Page, Track } from '../models/music.models';

@Injectable({ providedIn: 'root' })
export class AlbumService {
  constructor(private readonly http: HttpClient) {}

  list(size = 40) {
    return this.http.get<Page<Album>>(`${environment.apiUrl}/albums`, { params: { size } });
  }

  get(id: string) {
    return this.http.get<Album>(`${environment.apiUrl}/albums/${id}`);
  }

  tracks(id: string) {
    return this.http.get<Track[]>(`${environment.apiUrl}/albums/${id}/tracks`);
  }
}
