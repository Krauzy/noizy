import { AsyncPipe } from '@angular/common';
import { Component } from '@angular/core';
import { IconDefinition } from '@fortawesome/fontawesome-svg-core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faChevronLeft, faChevronRight, faGear, faHeart, faHouse, faList, faUpload } from '@fortawesome/free-solid-svg-icons';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { UserRole } from '../../core/models/music.models';
import { AuthService } from '../../core/services/auth.service';

interface SidebarLink {
  path: string;
  label: string;
  icon: IconDefinition;
  exact: boolean;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [AsyncPipe, FontAwesomeModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {
  readonly icons = {
    collapse: faChevronLeft,
    expand: faChevronRight,
    upload: faUpload
  };
  readonly mainLinks: SidebarLink[] = [
    { path: '/', label: 'Home', icon: faHouse, exact: true }
  ];
  readonly userLinks: SidebarLink[] = [
    { path: '/playlists', label: 'My Playlists', icon: faList, exact: false },
    { path: '/liked', label: 'Liked Songs', icon: faHeart, exact: false },
    { path: '/settings', label: 'Settings', icon: faGear, exact: false }
  ];
  readonly user$ = this.auth.user$;
  collapsed = false;

  constructor(private readonly auth: AuthService) {}

  toggleCollapsed(): void {
    this.collapsed = !this.collapsed;
  }

  canUpload(role: UserRole): boolean {
    return role === 'ARTIST' || role === 'ADMIN';
  }
}
