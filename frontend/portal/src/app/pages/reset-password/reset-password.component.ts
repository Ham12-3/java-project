import { Component } from '@angular/core';
import { AuthService } from '../../auth.service';
import { PortalStateService } from '../../portal-state.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent {
  form = { token: '', newPassword: '' };
  submitting = false;

  constructor(
    private readonly auth: AuthService,
    private readonly portalState: PortalStateService
  ) {}

  submit() {
    if (this.submitting) {
      return;
    }
    this.portalState.setError('');
    this.submitting = true;
    this.auth.confirmPasswordReset(this.form).subscribe({
      next: () => {
        this.portalState.setResetMessage('Password updated. You can sign in with the new credentials.');
        this.form = { token: '', newPassword: '' };
      },
      error: (err) => this.portalState.setError(err.error ?? err.message),
      complete: () => (this.submitting = false)
    });
  }
}
