import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environments';

export interface CustomerSummary {
  id: number;
  fullName: string;
  email: string;
  status: string;
  createdAt: string;
  companyName?: string;
  country?: string;
  industry?: string;
  riskScore?: number;
  verificationStatus?: string;
}

export interface CustomerDocument {
  id: number;
  name: string;
  type?: string;
  url?: string;
  status: string;
  contentType?: string;
  downloadable: boolean;
  uploadedAt: string;
}

export interface CustomerAuditEntry {
  id: number;
  action: string;
  actor?: string;
  notes?: string;
  createdAt: string;
}

export interface CustomerProfileDetails {
  summary: CustomerSummary;
  notes?: string;
  submittedAt?: string;
  approvedAt?: string;
  documents: CustomerDocument[];
  auditLog: CustomerAuditEntry[];
  account: AccountSummary;
}

export interface UpdateCustomerProfilePayload {
  companyName?: string;
  country?: string;
  industry?: string;
  riskScore?: number;
  verificationStatus?: string;
  notes?: string;
  updatedBy?: string;
}

export interface CreateCustomerDocumentPayload {
  name: string;
  type?: string;
  url?: string;
  status?: string;
  uploadedBy?: string;
}

export interface CustomerEventSummary {
  customerId: number;
  customerName: string;
  status: string;
  occurredAt: string;
}

export interface OpsAlert {
  id: string;
  type: string;
  severity: string;
  description: string;
  status: string;
  createdAt: string;
}

export interface OpsMetrics {
  onboardingCounts: Record<string, number>;
  recentApprovals: CustomerEventSummary[];
  recentRejections: CustomerEventSummary[];
  alerts: OpsAlert[];
}

export interface AccountSummary {
  provisioned: boolean;
  accountNumber?: string;
  sortCode?: string;
  currency: string;
  balance: number;
  overdraftLimit: number;
  availableBalance: number;
}

@Injectable({ providedIn: 'root' })
export class CustomerService {
  private readonly baseUrl = environment.apiBaseUrl;

  constructor(private readonly http: HttpClient) {}

  listCustomers() {
    return this.http.get<CustomerSummary[]>(`${this.baseUrl}/api/customers`);
  }

  getCustomerProfile(id: number) {
    return this.http.get<CustomerProfileDetails>(`${this.baseUrl}/api/customers/${id}`);
  }

  updateCustomerProfile(id: number, payload: UpdateCustomerProfilePayload) {
    return this.http.patch<CustomerProfileDetails>(`${this.baseUrl}/api/customers/${id}`, payload);
  }

  addDocument(id: number, payload: CreateCustomerDocumentPayload) {
    return this.http.post<CustomerProfileDetails>(`${this.baseUrl}/api/customers/${id}/documents`, payload);
  }

  getAuditEntries(id: number) {
    return this.http.get<CustomerAuditEntry[]>(`${this.baseUrl}/api/customers/${id}/audit`);
  }

  getOpsMetrics() {
    return this.http.get<OpsMetrics>(`${this.baseUrl}/api/ops/metrics`);
  }
}
