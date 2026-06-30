import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { artistGuard } from './core/guards/role.guard';
import { LoginPageComponent } from './pages/login-page/login-page.component';
import { RegisterPageComponent } from './pages/register-page/register-page.component';
import { HomePageComponent } from './pages/home-page/home-page.component';
import { ArtistDetailsPageComponent } from './pages/artist-details-page/artist-details-page.component';
import { AlbumDetailsPageComponent } from './pages/album-details-page/album-details-page.component';
import { PlaylistDetailsPageComponent } from './pages/playlist-details-page/playlist-details-page.component';
import { MyPlaylistsPageComponent } from './pages/my-playlists-page/my-playlists-page.component';
import { LikedSongsPageComponent } from './pages/liked-songs-page/liked-songs-page.component';
import { UploadTrackPageComponent } from './pages/upload-track-page/upload-track-page.component';
import { SettingsPageComponent } from './pages/settings-page/settings-page.component';
import { TrackDetailsPageComponent } from './pages/track-details-page/track-details-page.component';

export const routes: Routes = [
  { path: '', component: HomePageComponent },
  { path: 'login', component: LoginPageComponent },
  { path: 'register', component: RegisterPageComponent },
  { path: 'artists/:id', component: ArtistDetailsPageComponent },
  { path: 'tracks/:id', component: TrackDetailsPageComponent },
  { path: 'albums/:id', component: AlbumDetailsPageComponent },
  { path: 'playlists/:id', component: PlaylistDetailsPageComponent },
  { path: 'playlists', component: MyPlaylistsPageComponent, canActivate: [authGuard] },
  { path: 'liked', component: LikedSongsPageComponent, canActivate: [authGuard] },
  { path: 'upload', component: UploadTrackPageComponent, canActivate: [authGuard, artistGuard] },
  { path: 'settings', component: SettingsPageComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: '' }
];
