import { HttpErrorResponse, HttpHeaders, HttpInterceptorFn, HttpRequest, HttpResponse } from '@angular/common/http';
import { Observable, delay, of, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Album, Artist, AuthResponse, Page, PlaybackHistory, Playlist, Track, User } from '../models/music.models';

const LATENCY_MS = 180;
const NOW = '2026-06-27T12:00:00.000Z';
const MOCK_AUDIO =
  'data:audio/wav;base64,UklGRiQAAABXQVZFZm10IBAAAAABAAEAESsAACJWAAACABAAZGF0YQAAAAA=';

const jsonHeaders = new HttpHeaders({ 'Content-Type': 'application/json' });

let users: User[] = [
  {
    id: 'user-1',
    name: 'Noizy Listener',
    email: 'demo@noizy.local',
    role: 'USER',
    createdAt: NOW
  },
  {
    id: 'admin-1',
    name: 'Noizy Admin',
    email: 'admin@noizy.local',
    role: 'ADMIN',
    createdAt: NOW
  }
];

let currentUser = users[0];

let artists: Artist[] = [
  {
    id: 'artist-aurora',
    name: 'Aurora Lane',
    description: 'Warm synths, clean hooks, and late-night pop textures.',
    imageUrl: null,
    createdAt: NOW,
    updatedAt: NOW
  },
  {
    id: 'artist-neon',
    name: 'Neon Harbor',
    description: 'Indie electronic project built around wide drums and bright leads.',
    imageUrl: null,
    createdAt: NOW,
    updatedAt: NOW
  },
  {
    id: 'artist-rio',
    name: 'Rio Static',
    description: 'Brazilian groove references with modern streaming production.',
    imageUrl: null,
    createdAt: NOW,
    updatedAt: NOW
  }
];

let albums: Album[] = [
  {
    id: 'album-yellow-room',
    title: 'Yellow Room Sessions',
    artist: artists[0],
    coverUrl: null,
    releaseDate: '2026-04-18',
    createdAt: NOW,
    updatedAt: NOW
  },
  {
    id: 'album-night-swells',
    title: 'Night Swells',
    artist: artists[1],
    coverUrl: null,
    releaseDate: '2026-03-02',
    createdAt: NOW,
    updatedAt: NOW
  },
  {
    id: 'album-solar-loop',
    title: 'Solar Loop',
    artist: artists[2],
    coverUrl: null,
    releaseDate: '2026-05-11',
    createdAt: NOW,
    updatedAt: NOW
  }
];

let tracks: Track[] = [
  {
    id: 'track-city-lights',
    title: 'City Lights Afterglow',
    artist: artists[0],
    album: albums[0],
    genre: 'Synth Pop',
    durationSeconds: 196,
    audioS3Key: 'mock/audio/city-lights.wav',
    coverS3Key: null,
    playCount: 12840,
    streamUrl: MOCK_AUDIO,
    createdAt: NOW,
    updatedAt: NOW
  },
  {
    id: 'track-bright-noise',
    title: 'Bright Noise',
    artist: artists[0],
    album: albums[0],
    genre: 'Alt Pop',
    durationSeconds: 214,
    audioS3Key: 'mock/audio/bright-noise.wav',
    coverS3Key: null,
    playCount: 9840,
    streamUrl: MOCK_AUDIO,
    createdAt: NOW,
    updatedAt: NOW
  },
  {
    id: 'track-harbor-run',
    title: 'Harbor Run',
    artist: artists[1],
    album: albums[1],
    genre: 'Electronic',
    durationSeconds: 241,
    audioS3Key: 'mock/audio/harbor-run.wav',
    coverS3Key: null,
    playCount: 22410,
    streamUrl: MOCK_AUDIO,
    createdAt: NOW,
    updatedAt: NOW
  },
  {
    id: 'track-low-tide',
    title: 'Low Tide Signal',
    artist: artists[1],
    album: albums[1],
    genre: 'Downtempo',
    durationSeconds: 188,
    audioS3Key: 'mock/audio/low-tide.wav',
    coverS3Key: null,
    playCount: 6370,
    streamUrl: MOCK_AUDIO,
    createdAt: NOW,
    updatedAt: NOW
  },
  {
    id: 'track-samba-grid',
    title: 'Samba Grid',
    artist: artists[2],
    album: albums[2],
    genre: 'Groove',
    durationSeconds: 173,
    audioS3Key: 'mock/audio/samba-grid.wav',
    coverS3Key: null,
    playCount: 17320,
    streamUrl: MOCK_AUDIO,
    createdAt: NOW,
    updatedAt: NOW
  },
  {
    id: 'track-solar-loop',
    title: 'Solar Loop',
    artist: artists[2],
    album: albums[2],
    genre: 'House',
    durationSeconds: 226,
    audioS3Key: 'mock/audio/solar-loop.wav',
    coverS3Key: null,
    playCount: 14190,
    streamUrl: MOCK_AUDIO,
    createdAt: NOW,
    updatedAt: NOW
  }
];

let likedTrackIds = new Set<string>(['track-city-lights', 'track-samba-grid']);

let playlists: Playlist[] = [
  {
    id: 'playlist-focus',
    name: 'Focus Mix',
    description: 'Clean momentum for coding and design review.',
    owner: currentUser,
    isPublic: true,
    tracks: [tracks[0], tracks[2], tracks[5]],
    createdAt: NOW,
    updatedAt: NOW
  },
  {
    id: 'playlist-weekend',
    name: 'Weekend Uploads',
    description: 'Recent discoveries waiting for another listen.',
    owner: currentUser,
    isPublic: false,
    tracks: [tracks[1], tracks[4]],
    createdAt: NOW,
    updatedAt: NOW
  }
];

let playbackHistory: PlaybackHistory[] = [
  {
    id: 'history-1',
    track: tracks[0],
    playedAt: '2026-06-27T10:40:00.000Z'
  },
  {
    id: 'history-2',
    track: tracks[4],
    playedAt: '2026-06-27T09:12:00.000Z'
  }
];

export const mockBackendInterceptor: HttpInterceptorFn = (req, next) => {
  if (!environment.useMocks) {
    return next(req);
  }

  const path = apiPath(req.urlWithParams);
  if (!path) {
    return next(req);
  }

  return handleMockRequest(req, path);
};

function handleMockRequest(req: HttpRequest<unknown>, path: string): Observable<HttpResponse<unknown>> {
  const method = req.method.toUpperCase();
  const segments = path.split('/').filter(Boolean);
  const [resource, id, action, nestedId] = segments;

  if (resource === 'auth') {
    return handleAuth(req, method, id);
  }

  if (resource === 'users' && method === 'GET' && id === 'me') {
    return respond(currentUser);
  }

  if (resource === 'artists') {
    return handleArtists(req, method, id);
  }

  if (resource === 'albums') {
    return handleAlbums(req, method, id, action);
  }

  if (resource === 'tracks') {
    return handleTracks(req, method, id, action);
  }

  if (resource === 'playlists') {
    return handlePlaylists(req, method, id, action, nestedId);
  }

  if (resource === 'playback') {
    return handlePlayback(method, id);
  }

  return fail(404, `No mock route for ${method} ${path}`);
}

function handleAuth(req: HttpRequest<unknown>, method: string, action?: string): Observable<HttpResponse<unknown>> {
  if (method === 'POST' && action === 'login') {
    const body = req.body as Partial<{ email: string }>;
    currentUser = users.find((user) => user.email === body.email) ?? currentUser;
    return respond(authResponse(currentUser));
  }

  if (method === 'POST' && action === 'register') {
    const body = req.body as Partial<{ name: string; email: string }>;
    const user: User = {
      id: newId('user'),
      name: body.name?.trim() || 'Noizy User',
      email: body.email?.trim() || `user-${Date.now()}@noizy.local`,
      role: 'USER',
      createdAt: new Date().toISOString()
    };
    users = [user, ...users];
    currentUser = user;
    return respond(authResponse(user));
  }

  if (method === 'GET' && action === 'me') {
    return respond(currentUser);
  }

  return fail(404, `No mock auth route for ${method} ${action ?? ''}`);
}

function handleArtists(req: HttpRequest<unknown>, method: string, id?: string): Observable<HttpResponse<unknown>> {
  if (method === 'GET' && !id) {
    return respond(pageOf(artists, req));
  }

  if (method === 'GET' && id) {
    return respond(findOrFail(artists, id, 'artist'));
  }

  if (method === 'POST') {
    const body = req.body as Partial<Artist>;
    const artist: Artist = {
      id: newId('artist'),
      name: body.name?.trim() || 'New Artist',
      description: body.description ?? null,
      imageUrl: body.imageUrl ?? null,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };
    artists = [artist, ...artists];
    return respond(artist, 201);
  }

  if (method === 'PUT' && id) {
    const body = req.body as Partial<Artist>;
    const existing = findOrFail(artists, id, 'artist');
    const updated: Artist = {
      ...existing,
      name: body.name?.trim() || existing.name,
      description: body.description ?? existing.description,
      imageUrl: body.imageUrl ?? existing.imageUrl,
      updatedAt: new Date().toISOString()
    };
    artists = artists.map((artist) => artist.id === id ? updated : artist);
    return respond(updated);
  }

  if (method === 'DELETE' && id) {
    artists = artists.filter((artist) => artist.id !== id);
    return respond(null, 204);
  }

  return fail(404, `No mock artists route for ${method}`);
}

function handleAlbums(req: HttpRequest<unknown>, method: string, id?: string, action?: string): Observable<HttpResponse<unknown>> {
  if (method === 'GET' && !id) {
    return respond(pageOf(albums, req));
  }

  if (method === 'GET' && id && action === 'tracks') {
    return respond(tracks.filter((track) => track.album?.id === id));
  }

  if (method === 'GET' && id) {
    return respond(findOrFail(albums, id, 'album'));
  }

  if (method === 'POST') {
    const body = req.body as Partial<Album> & { artistId?: string };
    const artist = artists.find((item) => item.id === body.artistId) ?? artists[0];
    const album: Album = {
      id: newId('album'),
      title: body.title?.trim() || 'Untitled Album',
      artist,
      coverUrl: body.coverUrl ?? null,
      releaseDate: body.releaseDate ?? null,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };
    albums = [album, ...albums];
    return respond(album, 201);
  }

  if (method === 'PUT' && id) {
    const body = req.body as Partial<Album> & { artistId?: string };
    const existing = findOrFail(albums, id, 'album');
    const artist = artists.find((item) => item.id === body.artistId) ?? existing.artist;
    const updated: Album = {
      ...existing,
      title: body.title?.trim() || existing.title,
      artist,
      coverUrl: body.coverUrl ?? existing.coverUrl,
      releaseDate: body.releaseDate ?? existing.releaseDate,
      updatedAt: new Date().toISOString()
    };
    albums = albums.map((album) => album.id === id ? updated : album);
    return respond(updated);
  }

  if (method === 'DELETE' && id) {
    albums = albums.filter((album) => album.id !== id);
    tracks = tracks.map((track) => track.album?.id === id ? { ...track, album: null } : track);
    return respond(null, 204);
  }

  return fail(404, `No mock albums route for ${method}`);
}

function handleTracks(req: HttpRequest<unknown>, method: string, id?: string, action?: string): Observable<HttpResponse<unknown>> {
  if (method === 'POST' && id === 'upload') {
    return handleUpload(req);
  }

  if (method === 'GET' && id === 'search') {
    const query = req.params.get('query')?.toLowerCase().trim() ?? '';
    return respond(pageOf(tracks.filter((track) => trackMatchesQuery(track, query)), req));
  }

  if (method === 'GET' && id === 'liked') {
    return respond(tracks.filter((track) => likedTrackIds.has(track.id)));
  }

  if (method === 'GET' && !id) {
    return respond(pageOf(tracks, req));
  }

  if (method === 'GET' && id && action === 'stream') {
    return respond(silentAudioBlob(), 200, new HttpHeaders({ 'Content-Type': 'audio/wav' }));
  }

  if (method === 'GET' && id) {
    return respond(findOrFail(tracks, id, 'track'));
  }

  if (method === 'PUT' && id) {
    const body = req.body as Partial<Track> & { artistId?: string; albumId?: string };
    const existing = findOrFail(tracks, id, 'track');
    const artist = artists.find((item) => item.id === body.artistId) ?? existing.artist;
    const album = albums.find((item) => item.id === body.albumId) ?? existing.album ?? null;
    const updated: Track = {
      ...existing,
      title: body.title?.trim() || existing.title,
      artist,
      album,
      genre: body.genre ?? existing.genre,
      durationSeconds: body.durationSeconds ?? existing.durationSeconds,
      updatedAt: new Date().toISOString()
    };
    tracks = tracks.map((track) => track.id === id ? updated : track);
    return respond(updated);
  }

  if (method === 'DELETE' && id && action === 'like') {
    likedTrackIds.delete(id);
    return respond(null, 204);
  }

  if (method === 'POST' && id && action === 'like') {
    likedTrackIds.add(id);
    return respond(findOrFail(tracks, id, 'track'));
  }

  if (method === 'DELETE' && id) {
    tracks = tracks.filter((track) => track.id !== id);
    likedTrackIds.delete(id);
    playlists = playlists.map((playlist) => ({
      ...playlist,
      tracks: playlist.tracks.filter((track) => track.id !== id)
    }));
    return respond(null, 204);
  }

  return fail(404, `No mock tracks route for ${method}`);
}

function handleUpload(req: HttpRequest<unknown>): Observable<HttpResponse<unknown>> {
  const form = req.body instanceof FormData ? req.body : new FormData();
  const artistId = String(form.get('artistId') ?? artists[0].id);
  const albumId = String(form.get('albumId') ?? '');
  const artist = artists.find((item) => item.id === artistId) ?? artists[0];
  const album = albums.find((item) => item.id === albumId) ?? null;
  const title = String(form.get('title') ?? 'Uploaded Mock Track');
  const track: Track = {
    id: newId('track'),
    title,
    artist,
    album,
    genre: String(form.get('genre') ?? 'Upload'),
    durationSeconds: Number(form.get('durationSeconds') ?? 0),
    audioS3Key: `mock/audio/${slug(title)}.wav`,
    coverS3Key: null,
    playCount: 0,
    streamUrl: MOCK_AUDIO,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  };
  tracks = [track, ...tracks];
  return respond({ track, audioS3Key: track.audioS3Key, coverS3Key: track.coverS3Key }, 201);
}

function handlePlaylists(
  req: HttpRequest<unknown>,
  method: string,
  id?: string,
  action?: string,
  nestedId?: string
): Observable<HttpResponse<unknown>> {
  if (method === 'GET' && id === 'me') {
    return respond(playlists.filter((playlist) => playlist.owner.id === currentUser.id));
  }

  if (method === 'GET' && id === 'public') {
    return respond(playlists.filter((playlist) => playlist.isPublic));
  }

  if (method === 'GET' && id) {
    return respond(findOrFail(playlists, id, 'playlist'));
  }

  if (method === 'POST' && !id) {
    const body = req.body as Partial<Playlist>;
    const playlist: Playlist = {
      id: newId('playlist'),
      name: body.name?.trim() || 'New Playlist',
      description: body.description ?? '',
      owner: currentUser,
      isPublic: Boolean(body.isPublic),
      tracks: [],
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };
    playlists = [playlist, ...playlists];
    return respond(playlist, 201);
  }

  if (method === 'PUT' && id) {
    const body = req.body as Partial<Playlist>;
    const existing = findOrFail(playlists, id, 'playlist');
    const updated: Playlist = {
      ...existing,
      name: body.name?.trim() || existing.name,
      description: body.description ?? existing.description,
      isPublic: body.isPublic ?? existing.isPublic,
      updatedAt: new Date().toISOString()
    };
    playlists = playlists.map((playlist) => playlist.id === id ? updated : playlist);
    return respond(updated);
  }

  if (method === 'POST' && id && action === 'tracks' && nestedId) {
    const playlist = findOrFail(playlists, id, 'playlist');
    const track = findOrFail(tracks, nestedId, 'track');
    const updated: Playlist = {
      ...playlist,
      tracks: playlist.tracks.some((item) => item.id === nestedId) ? playlist.tracks : [...playlist.tracks, track],
      updatedAt: new Date().toISOString()
    };
    playlists = playlists.map((item) => item.id === id ? updated : item);
    return respond(updated);
  }

  if (method === 'DELETE' && id && action === 'tracks' && nestedId) {
    const playlist = findOrFail(playlists, id, 'playlist');
    const updated: Playlist = {
      ...playlist,
      tracks: playlist.tracks.filter((track) => track.id !== nestedId),
      updatedAt: new Date().toISOString()
    };
    playlists = playlists.map((item) => item.id === id ? updated : item);
    return respond(updated);
  }

  return fail(404, `No mock playlists route for ${method}`);
}

function handlePlayback(method: string, trackId?: string): Observable<HttpResponse<unknown>> {
  if (method === 'GET' && trackId === 'history') {
    return respond(playbackHistory);
  }

  if (method === 'POST' && trackId) {
    const track = findOrFail(tracks, trackId, 'track');
    const historyItem: PlaybackHistory = {
      id: newId('history'),
      track,
      playedAt: new Date().toISOString()
    };
    playbackHistory = [historyItem, ...playbackHistory].slice(0, 20);
    tracks = tracks.map((item) => item.id === trackId ? { ...item, playCount: item.playCount + 1 } : item);
    return respond(historyItem, 201);
  }

  return fail(404, `No mock playback route for ${method}`);
}

function authResponse(user: User): AuthResponse {
  return {
    token: `mock-token-${user.id}`,
    user
  };
}

function apiPath(urlWithParams: string): string | null {
  const url = new URL(urlWithParams, window.location.origin);
  const apiIndex = url.pathname.indexOf('/api');

  if (apiIndex < 0) {
    return null;
  }

  return url.pathname.slice(apiIndex + '/api'.length) || '/';
}

function pageOf<T>(items: T[], req: HttpRequest<unknown>): Page<T> {
  const requestedPage = Number(req.params.get('page') ?? 0);
  const requestedSize = Number(req.params.get('size') ?? items.length);
  const number = Number.isFinite(requestedPage) ? Math.max(0, requestedPage) : 0;
  const size = Number.isFinite(requestedSize) ? Math.max(1, requestedSize) : Math.max(1, items.length);
  const start = number * size;
  const content = items.slice(start, start + size);

  return {
    content,
    totalElements: items.length,
    totalPages: Math.max(1, Math.ceil(items.length / size)),
    number,
    size
  };
}

function trackMatchesQuery(track: Track, query: string): boolean {
  if (!query) return true;

  return [
    track.title,
    track.artist.name,
    track.album?.title,
    track.genre
  ].some((value) => value?.toLowerCase().includes(query));
}

function findOrFail<T extends { id: string }>(items: T[], id: string, label: string): T {
  const item = items.find((candidate) => candidate.id === id);

  if (!item) {
    throw new HttpErrorResponse({
      status: 404,
      statusText: 'Not Found',
      error: { message: `Mock ${label} not found` }
    });
  }

  return item;
}

function respond<T>(body: T, status = 200, headers = jsonHeaders): Observable<HttpResponse<unknown>> {
  return of(new HttpResponse<T>({ body, status, headers })).pipe(delay(LATENCY_MS));
}

function fail(status: number, message: string): Observable<never> {
  return throwError(() => new HttpErrorResponse({
    status,
    statusText: status === 404 ? 'Not Found' : 'Mock Error',
    error: { message }
  }));
}

function silentAudioBlob(): Blob {
  const base64 = MOCK_AUDIO.split(',')[1];
  const bytes = Uint8Array.from(atob(base64), (char) => char.charCodeAt(0));

  return new Blob([bytes], { type: 'audio/wav' });
}

function newId(prefix: string): string {
  return `${prefix}-${Date.now()}-${Math.floor(Math.random() * 1000)}`;
}

function slug(value: string): string {
  return value.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '') || 'track';
}
