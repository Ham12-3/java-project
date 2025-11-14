import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../auth.service';
import { PortalStateService } from '../../portal-state.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  form = { username: '', email: '', password: '', mfaEnabled: false };
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
    this.auth.register(this.form).subscribe({
      next: () => {
        this.portalState.setToast('Account created. Please sign in.');
        this.router.navigate(['/auth/login']);
      },
      error: (err) => this.portalState.setError(err.error ?? err.message),
      complete: () => (this.submitting = false)
    });
  }
}
