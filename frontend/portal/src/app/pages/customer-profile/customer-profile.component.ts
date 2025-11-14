import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {
  CreateCustomerDocumentPayload,
  CustomerProfileDetails,
  CustomerService,
  UpdateCustomerProfilePayload
} from '../../customer.service';
import { PortalStateService } from '../../portal-state.service';
import { Subscription } from 'rxjs';
import { environment } from '../../../environments/environments';

@Component({
  selector: 'app-customer-profile',
  templateUrl: './customer-profile.component.html',
  styleUrls: ['./customer-profile.component.css']
})
export class CustomerProfileComponent implements OnInit, OnDestroy {
  profile?: CustomerProfileDetails;
  loading = false;
  savingProfile = false;
  addingDocument = false;
  private routeSub?: Subscription;

  readonly verificationStatuses = ['NOT_STARTED', 'IN_REVIEW', 'APPROVED', 'REJECTED'];
  readonly documentStatuses = ['UPLOADED', 'IN_REVIEW', 'APPROVED', 'REJECTED'];

  profileForm: {
    companyName: string;
    country: string;
    industry: string;
    riskScore: string;
    verificationStatus: string;
    notes: string;
    updatedBy: string;
  } = this.createProfileForm();

  documentForm: {
    name: string;
    type: string;
    url: string;
    status: string;
    uploadedBy: string;
  } = this.createDocumentForm();

  constructor(
    private readonly route: ActivatedRoute,
    private readonly customersApi: CustomerService,
    private readonly portalState: PortalStateService
  ) {}

  ngOnInit() {
    this.routeSub = this.route.paramMap.subscribe((params) => {
      const id = Number(params.get('id'));
      if (!Number.isNaN(id)) {
        this.loadProfile(id);
      }
    });
  }

  ngOnDestroy() {
    this.routeSub?.unsubscribe();
  }

  loadProfile(id: number) {
    this.loading = true;
    this.portalState.setError('');
    this.customersApi.getCustomerProfile(id).subscribe({
      next: (profile) => {
        this.profile = profile;
        this.populateProfileForm();
      },
      error: (err) => this.portalState.setError(err.error ?? err.message),
      complete: () => (this.loading = false)
    });
  }

  saveProfile() {
    if (!this.profile) {
      return;
    }
    this.savingProfile = true;
    const payload: UpdateCustomerProfilePayload = {
      companyName: this.profileForm.companyName.trim() || undefined,
      country: this.profileForm.country.trim() || undefined,
      industry: this.profileForm.industry.trim() || undefined,
      riskScore: this.profileForm.riskScore ? Number(this.profileForm.riskScore) : undefined,
      verificationStatus: this.profileForm.verificationStatus || undefined,
      notes: this.profileForm.notes.trim() || undefined,
      updatedBy: this.profileForm.updatedBy.trim() || undefined
    };

    this.customersApi.updateCustomerProfile(this.profile.summary.id, payload).subscribe({
      next: (updated) => {
        this.profile = updated;
        this.populateProfileForm();
        this.portalState.setToast('Profile updated');
      },
      error: (err) => this.portalState.setError(err.error ?? err.message),
      complete: () => (this.savingProfile = false)
    });
  }

  addDocument() {
    if (!this.profile || !this.documentForm.name) {
      return;
    }
    this.addingDocument = true;
    const payload: CreateCustomerDocumentPayload = {
      name: this.documentForm.name.trim(),
      type: this.documentForm.type.trim() || undefined,
      url: this.documentForm.url.trim() || undefined,
      status: this.documentForm.status,
      uploadedBy: this.documentForm.uploadedBy.trim() || undefined
    };

    this.customersApi.addDocument(this.profile.summary.id, payload).subscribe({
      next: (updated) => {
        this.profile = updated;
        this.documentForm = this.createDocumentForm();
        this.portalState.setToast('Document attached');
      },
      error: (err) => this.portalState.setError(err.error ?? err.message),
      complete: () => (this.addingDocument = false)
    });
  }

  riskLabel() {
    if (!this.profile?.summary.riskScore && this.profile?.summary.riskScore !== 0) {
      return 'Unrated';
    }
    return `${this.profile.summary.riskScore?.toFixed(0)} / 100`;
  }

  private populateProfileForm() {
    if (!this.profile) {
      return;
    }
    this.profileForm = {
      companyName: this.profile.summary.companyName || '',
      country: this.profile.summary.country || '',
      industry: this.profile.summary.industry || '',
      riskScore: this.profile.summary.riskScore != null ? String(this.profile.summary.riskScore) : '',
      verificationStatus: this.profile.summary.verificationStatus || 'NOT_STARTED',
      notes: this.profile.notes || '',
      updatedBy: ''
    };
  }

  private createProfileForm() {
    return {
      companyName: '',
      country: '',
      industry: '',
      riskScore: '',
      verificationStatus: 'NOT_STARTED',
      notes: '',
      updatedBy: ''
    };
  }

  private createDocumentForm() {
    return {
      name: '',
      type: '',
      url: '',
      status: 'UPLOADED',
      uploadedBy: ''
    };
  }

  hasAccount() {
    return !!this.profile?.account?.provisioned;
  }

  accountNumber() {
    return this.profile?.account?.accountNumber?.replace(/(\d{4})(\d{4})/, '$1 $2');
  }

  downloadDocument(docId: number) {
    const url = `${environment.apiBaseUrl}/api/customers/${this.profile?.summary.id}/documents/${docId}/download`;
    window.open(url, '_blank');
  }
}
