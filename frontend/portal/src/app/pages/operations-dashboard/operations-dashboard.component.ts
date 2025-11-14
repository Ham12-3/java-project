import { Component, OnInit } from '@angular/core';
import { CustomerService, OpsAlert, OpsMetrics } from '../../customer.service';
import { PortalStateService } from '../../portal-state.service';

@Component({
  selector: 'app-operations-dashboard',
  templateUrl: './operations-dashboard.component.html',
  styleUrls: ['./operations-dashboard.component.css']
})
export class OperationsDashboardComponent implements OnInit {
  metrics?: OpsMetrics;
  loading = false;

  constructor(
    private readonly customersApi: CustomerService,
    private readonly portalState: PortalStateService
  ) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.loading = true;
    this.portalState.setError('');
    this.customersApi.getOpsMetrics().subscribe({
      next: (metrics) => (this.metrics = metrics),
      error: (err) => this.portalState.setError(err.error ?? err.message),
      complete: () => (this.loading = false)
    });
  }

  onboardingEntries() {
    if (!this.metrics) {
      return [];
    }
    return Object.entries(this.metrics.onboardingCounts);
  }

  alertClass(alert: OpsAlert) {
    return alert.severity.toLowerCase();
  }
}
