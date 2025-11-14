import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../auth.service';
import { PortalStateService } from '../../portal-state.service';

@Component({
  selector: 'app-customer-onboarding',
  templateUrl: './customer-onboarding.component.html',
  styleUrls: ['./customer-onboarding.component.css']
})
export class CustomerOnboardingComponent {
  form = { fullName: '', email: '', notes: '' };
  submitting = false;

  constructor(
    private readonly auth: AuthService,
    private readonly portalState: PortalStateService,
    private readonly router: Router
  ) {}

  submit() {
    if (this.submitting) {
      return;
    }
    this.portalState.setError('');
    this.submitting = true;
    this.auth.createCustomer(this.form).subscribe({
      next: () => {
        this.portalState.setToast('Customer added to pipeline');
        this.form = { fullName: '', email: '', notes: '' };
        this.router.navigate(['/onboarding/pipeline']);
      },
      error: (err) => this.portalState.setError(err.error ?? err.message),
      complete: () => (this.submitting = false)
    });
  }
}
