import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environments';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly baseUrl = environment.apiBaseUrl;

  constructor(private readonly http: HttpClient) {}

  register(payload: { username: string; email: string; password: string; mfaEnabled: boolean }) {
    return this.http.post(`${this.baseUrl}/api/auth/register`, payload);
  }

  login(payload: { username: string; password: string; mfaCode?: string }) {
    return this.http.post(`${this.baseUrl}/api/auth/login`, payload);
  }

  requestPasswordReset(payload: { email: string }) {
    return this.http.post<{ resetToken: string }>(`${this.baseUrl}/api/auth/request-reset`, payload);
  }

  confirmPasswordReset(payload: { token: string; newPassword: string }) {
    return this.http.post(`${this.baseUrl}/api/auth/confirm-reset`, payload);
  }

  createCustomer(payload: { fullName: string; email: string; notes?: string }) {
    return this.http.post<Customer>(`${this.baseUrl}/api/onboarding/customers`, payload);
  }

  listCustomers() {
    return this.http.get<Customer[]>(`${this.baseUrl}/api/onboarding/customers`);
  }

  updateCustomerStatus(id: number, status: string) {
    return this.http.patch<Customer>(`${this.baseUrl}/api/onboarding/customers/${id}/status`, { status });
  }
}

export interface Customer {
  id: number;
  fullName: string;
  email: string;
  notes?: string;
  status: string;
  createdAt: string;
}
