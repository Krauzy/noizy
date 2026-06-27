import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Artist, Page } from '../models/music.models';

@Injectable({ providedIn: 'root' })
export class ArtistService {
  constructor(private readonly http: HttpClient) {}

  list(size = 40) {
    return this.http.get<Page<Artist>>(`${environment.apiUrl}/artists`, { params: { size } });
  }

  get(id: string) {
    return this.http.get<Artist>(`${environment.apiUrl}/artists/${id}`);
  }
}
