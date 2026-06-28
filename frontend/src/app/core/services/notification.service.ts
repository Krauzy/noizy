import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type NotificationKind = 'success' | 'error' | 'info';

export interface AppNotification {
  id: number;
  kind: NotificationKind;
  title: string;
  message: string;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private nextId = 1;
  private readonly notificationsSubject = new BehaviorSubject<AppNotification[]>([]);
  readonly notifications$ = this.notificationsSubject.asObservable();

  success(title: string, message: string): void {
    this.push('success', title, message);
  }

  error(title: string, message: string): void {
    this.push('error', title, message);
  }

  info(title: string, message: string): void {
    this.push('info', title, message);
  }

  dismiss(id: number): void {
    this.notificationsSubject.next(this.notificationsSubject.value.filter((item) => item.id !== id));
  }

  private push(kind: NotificationKind, title: string, message: string): void {
    const item = { id: this.nextId++, kind, title, message };
    this.notificationsSubject.next([item, ...this.notificationsSubject.value].slice(0, 4));
    window.setTimeout(() => this.dismiss(item.id), 4800);
  }
}
