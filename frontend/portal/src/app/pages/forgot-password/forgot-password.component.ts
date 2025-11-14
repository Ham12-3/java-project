import { Component } from '@angular/core';
import { AuthService } from '../../auth.service';
import { PortalStateService } from '../../portal-state.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent {
  form = { email: '' };
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
    this.portalState.setResetMessage('');
    this.portalState.setResetToken('');
    this.submitting = true;
    this.auth.requestPasswordReset(this.form).subscribe({
      next: (res) => {
        this.portalState.setResetToken(res.resetToken ?? '');
        this.portalState.setResetMessage('If the email exists, a reset token has been issued.');
        this.form.email = '';
      },
      error: (err) => this.portalState.setError(err.error ?? err.message),
      complete: () => (this.submitting = false)
    });
  }
}
