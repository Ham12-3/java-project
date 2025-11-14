import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { PortalStateService } from '../../portal-state.service';

@Component({
  selector: 'app-status',
  templateUrl: './status.component.html',
  styleUrls: ['./status.component.css']
})
export class StatusComponent {
  lastToken$: Observable<string>;
  lastError$: Observable<string>;
  resetMessage$: Observable<string>;
  resetToken$: Observable<string>;

  constructor(private readonly portalState: PortalStateService) {
    this.lastToken$ = portalState.lastToken$;
    this.lastError$ = portalState.lastError$;
    this.resetMessage$ = portalState.resetMessage$;
    this.resetToken$ = portalState.resetToken$;
  }
}
