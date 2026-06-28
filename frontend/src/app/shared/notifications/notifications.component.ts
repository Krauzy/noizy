import { AsyncPipe } from '@angular/common';
import { Component } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faCircleCheck, faCircleExclamation, faCircleInfo, faXmark } from '@fortawesome/free-solid-svg-icons';
import { NotificationKind, NotificationService } from '../../core/services/notification.service';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [AsyncPipe, FontAwesomeModule],
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.scss']
})
export class NotificationsComponent {
  readonly notifications$ = this.notifications.notifications$;
  readonly icons = {
    close: faXmark,
    error: faCircleExclamation,
    info: faCircleInfo,
    success: faCircleCheck
  };

  constructor(readonly notifications: NotificationService) {}

  icon(kind: NotificationKind) {
    return this.icons[kind];
  }
}
