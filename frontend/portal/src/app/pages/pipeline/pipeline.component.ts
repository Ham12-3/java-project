import { Component, OnInit } from '@angular/core';
import { AuthService, Customer } from '../../auth.service';
import { PortalStateService } from '../../portal-state.service';

@Component({
  selector: 'app-pipeline',
  templateUrl: './pipeline.component.html',
  styleUrls: ['./pipeline.component.css']
})
export class PipelineComponent implements OnInit {
  customers: Customer[] = [];
  readonly statuses: ReadonlyArray<string> = ['PENDING', 'APPROVED', 'REJECTED'];
  loading = false;

  constructor(
    private readonly auth: AuthService,
    private readonly portalState: PortalStateService
  ) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.loading = true;
    this.portalState.setError('');
    this.auth.listCustomers().subscribe({
      next: (customers) => (this.customers = customers),
      error: (err) => this.portalState.setError(err.error ?? err.message),
      complete: () => (this.loading = false)
    });
  }

  updateStatus(customer: Customer, status: string) {
    if (customer.status === status) {
      return;
    }
    this.portalState.setError('');
    this.auth.updateCustomerStatus(customer.id, status).subscribe({
      next: (updated) => {
        this.customers = this.customers.map((c) => (c.id === updated.id ? updated : c));
        this.portalState.setToast(`Customer marked ${updated.status.toLowerCase()}`);
      },
      error: (err) => this.portalState.setError(err.error ?? err.message)
    });
  }
}
