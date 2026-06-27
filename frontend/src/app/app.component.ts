import { AsyncPipe } from '@angular/common';
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './shared/header/header.component';
import { SidebarComponent } from './shared/sidebar/sidebar.component';
import { AudioPlayerComponent } from './shared/audio-player/audio-player.component';
import { PlayerService } from './core/services/player.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [AsyncPipe, RouterOutlet, HeaderComponent, SidebarComponent, AudioPlayerComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  readonly currentTrack$ = this.player.currentTrack$;

  constructor(private readonly player: PlayerService) {}
}
