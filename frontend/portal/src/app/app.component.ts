import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { PortalStateService } from './portal-state.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  readonly sections = [
    {
      name: 'Authentication',
      description: 'Provision trusted operators with MFA using SendGrid OTP delivery.'
    },
    {
      name: 'Customer Onboarding',
      description: 'Review KYC packs from the mobile app and advance customers with one click.'
    },
    {
      name: 'Operations Console',
      description: 'Monitor GBP balances, audit events, and account lifecycle in real time.'
    }
  ];

  readonly toast$: Observable<string> = this.portalState.toast$;

  constructor(private readonly portalState: PortalStateService) {}
}
