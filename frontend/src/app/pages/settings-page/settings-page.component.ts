import { AsyncPipe } from '@angular/common';
import { Component } from '@angular/core';
import { UserRole } from '../../core/models/music.models';
import { AuthService } from '../../core/services/auth.service';
import { environment } from '../../../environments/environment';
import { NotificationService } from '../../core/services/notification.service';

@Component({
  selector: 'app-settings-page',
  standalone: true,
  imports: [AsyncPipe],
  templateUrl: './settings-page.component.html',
  styleUrls: ['./settings-page.component.scss']
})
export class SettingsPageComponent {
  readonly user$ = this.auth.user$;
  avatarUploading = false;
  avatarPreviewUrl = '';

  constructor(private readonly auth: AuthService, private readonly notifications: NotificationService) {}

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

  avatarUrl(user: { avatarUrl?: string | null }): string | null {
    if (!user.avatarUrl) return null;
    return user.avatarUrl.startsWith('http') ? user.avatarUrl : `${environment.apiBaseUrl}${user.avatarUrl}`;
  }

  avatarSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.item(0);
    if (!file) return;
    if (!file.type.startsWith('image/')) {
      this.notifications.error('Invalid avatar', 'Use an image file for your profile photo.');
      return;
    }

    if (this.avatarPreviewUrl) URL.revokeObjectURL(this.avatarPreviewUrl);
    this.avatarPreviewUrl = URL.createObjectURL(file);
    this.avatarUploading = true;
    this.auth.uploadAvatar(file).subscribe({
      next: () => {
        this.avatarUploading = false;
        this.notifications.success('Profile updated', 'Your profile photo was updated.');
      },
      error: () => {
        this.avatarUploading = false;
      }
    });
  }
}
