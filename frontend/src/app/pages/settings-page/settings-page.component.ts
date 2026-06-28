import { AsyncPipe } from '@angular/common';
import { Component } from '@angular/core';
import { UserRole } from '../../core/models/music.models';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-settings-page',
  standalone: true,
  imports: [AsyncPipe],
  templateUrl: './settings-page.component.html',
  styleUrls: ['./settings-page.component.scss']
})
export class SettingsPageComponent {
  readonly user$ = this.auth.user$;

  constructor(private readonly auth: AuthService) {}

  roleLabel(role: UserRole): string {
    const labels: Record<UserRole, string> = {
      FREE_TIER: 'Free-tier',
      SUBSCRIBER: 'Subscriber',
      ARTIST: 'Artist',
      ADMIN: 'Admin'
    };
    return labels[role];
  }

  canUpload(role: UserRole): boolean {
    return role === 'ARTIST' || role === 'ADMIN';
  }
}
