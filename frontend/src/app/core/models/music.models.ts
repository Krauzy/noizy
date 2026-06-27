export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface User {
  id: string;
  name: string;
  email: string;
  role: 'USER' | 'ADMIN';
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface Artist {
  id: string;
  name: string;
  description?: string | null;
  imageUrl?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface Album {
  id: string;
  title: string;
  artist: Artist;
  coverUrl?: string | null;
  releaseDate?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface Track {
  id: string;
  title: string;
  artist: Artist;
  album?: Album | null;
  genre?: string | null;
  durationSeconds: number;
  audioS3Key: string;
  coverS3Key?: string | null;
  playCount: number;
  streamUrl: string;
  createdAt: string;
  updatedAt: string;
}

export interface Playlist {
  id: string;
  name: string;
  description?: string | null;
  owner: User;
  isPublic: boolean;
  tracks: Track[];
  createdAt: string;
  updatedAt: string;
}

export interface PlaybackHistory {
  id: string;
  track: Track;
  playedAt: string;
}
