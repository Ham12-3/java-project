import { Component, OnInit } from '@angular/core';
import { CustomerService, CustomerSummary } from '../../customer.service';
import { PortalStateService } from '../../portal-state.service';

@Component({
  selector: 'app-customers',
  templateUrl: './customers.component.html',
  styleUrls: ['./customers.component.css']
})
export class CustomersComponent implements OnInit {
  customers: CustomerSummary[] = [];
  filtered: CustomerSummary[] = [];
  statusFilter = 'ALL';
  riskFilter = 'ALL';
  loading = false;

  readonly statuses = ['ALL', 'PENDING', 'APPROVED', 'REJECTED'];
  readonly riskBuckets = ['ALL', 'LOW', 'MEDIUM', 'HIGH', 'UNRATED'];

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
    this.customersApi.listCustomers().subscribe({
      next: (customers) => {
        this.customers = customers;
        this.applyFilters();
      },
      error: (err) => this.portalState.setError(err.error ?? err.message),
      complete: () => (this.loading = false)
    });
  }

  applyFilters() {
    this.filtered = this.customers.filter(
      (customer) => this.matchesStatus(customer) && this.matchesRisk(customer)
    );
  }

  private matchesStatus(customer: CustomerSummary) {
    return this.statusFilter === 'ALL' || customer.status === this.statusFilter;
  }

  private matchesRisk(customer: CustomerSummary) {
    if (this.riskFilter === 'ALL') {
      return true;
    }
    const bucket = this.riskBucket(customer);
    return bucket === this.riskFilter;
  }

  riskBucket(customer: CustomerSummary) {
    const riskScore = customer.riskScore ?? -1;
    if (riskScore < 0) {
      return 'UNRATED';
    }
    if (riskScore >= 70) {
      return 'HIGH';
    }
    if (riskScore >= 30) {
      return 'MEDIUM';
    }
    return 'LOW';
  }

  riskLabel(customer: CustomerSummary) {
    return customer.riskScore != null ? `${customer.riskScore.toFixed(0)} / 100` : 'Unrated';
  }
}
