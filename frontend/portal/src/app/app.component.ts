import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  readonly title = 'FinTech Platform Portal';
  readonly sections = [
    {
      name: 'Onboarding',
      description: 'Track KYC applications and pending approvals.'
    },
    {
      name: 'Accounts',
      description: 'Manage wallets, balances, and statements.'
    },
    {
      name: 'Payments',
      description: 'Initiate transfers and monitor settlement status.'
    },
    {
      name: 'Fraud & Risk',
      description: 'Review alerts, rules, and analytics insights.'
    }
  ];
}
