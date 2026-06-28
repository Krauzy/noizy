import { HttpClient } from '@angular/common/http';
import { Injectable, NgZone } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Track } from '../models/music.models';

export type LoopMode = 'off' | 'track' | 'queue';

@Injectable({ providedIn: 'root' })
export class PlayerService {
  private readonly audio = new Audio();
  private readonly queueSubject = new BehaviorSubject<Track[]>([]);
  private readonly currentSubject = new BehaviorSubject<Track | null>(null);
  private readonly playingSubject = new BehaviorSubject(false);
  private readonly progressSubject = new BehaviorSubject(0);
  private readonly currentTimeSubject = new BehaviorSubject(0);
  private readonly durationSubject = new BehaviorSubject(0);
  private readonly volumeSubject = new BehaviorSubject(0.8);
  private readonly loopModeSubject = new BehaviorSubject<LoopMode>('off');
  private readonly shuffleSubject = new BehaviorSubject(false);

  readonly queue$ = this.queueSubject.asObservable();
  readonly currentTrack$ = this.currentSubject.asObservable();
  readonly playing$ = this.playingSubject.asObservable();
  readonly progress$ = this.progressSubject.asObservable();
  readonly currentTime$ = this.currentTimeSubject.asObservable();
  readonly duration$ = this.durationSubject.asObservable();
  readonly volume$ = this.volumeSubject.asObservable();
  readonly loopMode$ = this.loopModeSubject.asObservable();
  readonly shuffle$ = this.shuffleSubject.asObservable();

  constructor(private readonly zone: NgZone, private readonly http: HttpClient) {
    this.audio.volume = 0.8;
    this.audio.preload = 'metadata';
    const updateDuration = () => {
      this.zone.run(() => this.durationSubject.next(this.safeDuration()));
    };
    this.audio.addEventListener('loadedmetadata', updateDuration);
    this.audio.addEventListener('durationchange', updateDuration);
    this.audio.addEventListener('timeupdate', () => {
      this.zone.run(() => {
        const duration = this.safeDuration();
        this.currentTimeSubject.next(this.audio.currentTime || 0);
        this.durationSubject.next(duration);
        this.progressSubject.next(duration ? (this.audio.currentTime / duration) * 100 : 0);
      });
    });
    this.audio.addEventListener('play', () => this.zone.run(() => this.playingSubject.next(true)));
    this.audio.addEventListener('pause', () => this.zone.run(() => this.playingSubject.next(false)));
    this.audio.addEventListener('error', () => this.zone.run(() => this.playingSubject.next(false)));
    this.audio.addEventListener('ended', () => this.zone.run(() => this.handleEnded()));
  }

  playTrack(track: Track, queue: Track[] = this.queueSubject.value): void {
    this.queueSubject.next(queue.length ? queue : [track]);
    this.currentSubject.next(track);
    this.progressSubject.next(0);
    this.currentTimeSubject.next(0);
    this.durationSubject.next(track.durationSeconds || 0);
    this.audio.src = this.streamUrl(track);
    this.audio.load();
    this.audio.play().then(() => this.playingSubject.next(true)).catch(() => this.playingSubject.next(false));
    this.http.post(`${environment.apiUrl}/playback/${track.id}`, {}).subscribe({ error: () => undefined });
  }

  toggle(): void {
    if (!this.currentSubject.value) return;
    if (this.audio.paused) {
      this.audio.play().then(() => this.playingSubject.next(true)).catch(() => this.playingSubject.next(false));
    } else {
      this.audio.pause();
      this.playingSubject.next(false);
    }
  }

  next(): void {
    const queue = this.queueSubject.value;
    const current = this.currentSubject.value;
    if (!queue.length || !current) return;
    const index = queue.findIndex((item) => item.id === current.id);
    const nextTrack = this.shuffleSubject.value ? this.randomTrack(queue, current) : queue[(index + 1) % queue.length];
    this.playTrack(nextTrack, queue);
  }

  previous(): void {
    const queue = this.queueSubject.value;
    const current = this.currentSubject.value;
    if (!queue.length || !current) return;
    const index = queue.findIndex((item) => item.id === current.id);
    const previousTrack = queue[(index - 1 + queue.length) % queue.length];
    this.playTrack(previousTrack, queue);
  }

  seek(percent: number): void {
    const duration = this.safeDuration();
    if (!duration) return;
    const nextTime = (Math.min(100, Math.max(0, percent)) / 100) * duration;
    this.audio.currentTime = nextTime;
    this.currentTimeSubject.next(nextTime);
    this.progressSubject.next(duration ? (nextTime / duration) * 100 : 0);
  }

  setVolume(value: number): void {
    const volume = Math.min(1, Math.max(0, value));
    this.audio.volume = volume;
    this.volumeSubject.next(volume);
  }

  cycleLoopMode(): void {
    const nextMode: Record<LoopMode, LoopMode> = {
      off: 'track',
      track: 'queue',
      queue: 'off'
    };

    this.loopModeSubject.next(nextMode[this.loopModeSubject.value]);
  }

  toggleShuffle(): void {
    this.shuffleSubject.next(!this.shuffleSubject.value);
  }

  private handleEnded(): void {
    if (this.loopModeSubject.value === 'track') {
      this.replayCurrentTrack();
      return;
    }

    const queue = this.queueSubject.value;
    const current = this.currentSubject.value;

    if (!queue.length || !current) {
      this.stopAtEnd();
      return;
    }

    if (this.shuffleSubject.value && queue.length > 1) {
      this.playTrack(this.randomTrack(queue, current), queue);
      return;
    }

    const index = queue.findIndex((item) => item.id === current.id);
    const nextTrack = index >= 0 ? queue[index + 1] : undefined;

    if (nextTrack) {
      this.playTrack(nextTrack, queue);
      return;
    }

    if (this.loopModeSubject.value === 'queue') {
      this.playTrack(queue[0], queue);
      return;
    }

    this.stopAtEnd();
  }

  private replayCurrentTrack(): void {
    this.audio.currentTime = 0;
    this.progressSubject.next(0);
    this.currentTimeSubject.next(0);
    this.audio.play().then(() => this.playingSubject.next(true)).catch(() => this.playingSubject.next(false));
  }

  private stopAtEnd(): void {
    this.playingSubject.next(false);
    this.currentTimeSubject.next(this.safeDuration() || this.currentTimeSubject.value);
    this.durationSubject.next(this.safeDuration() || this.durationSubject.value);
    this.progressSubject.next(100);
  }

  private randomTrack(queue: Track[], current: Track): Track {
    if (queue.length <= 1) return current;

    const candidates = queue.filter((track) => track.id !== current.id);
    if (!candidates.length) return current;

    return candidates[Math.floor(Math.random() * candidates.length)];
  }

  private safeDuration(): number {
    if (Number.isFinite(this.audio.duration) && this.audio.duration > 0) {
      return this.audio.duration;
    }

    return this.currentSubject.value?.durationSeconds || 0;
  }

  private streamUrl(track: Track): string {
    if (track.streamUrl.startsWith('http') || track.streamUrl.startsWith('data:') || track.streamUrl.startsWith('blob:')) {
      return track.streamUrl;
    }

    return environment.apiBaseUrl ? `${environment.apiBaseUrl}${track.streamUrl}` : track.streamUrl;
  }
}
