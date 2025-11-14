import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../auth.service';
import { PortalStateService } from '../../portal-state.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  form = { username: '', password: '', mfaCode: '' };
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
    this.auth.login(this.form).subscribe({
      next: (res: any) => {
        this.portalState.setToken(res.token);
        this.portalState.setToast('Signed in successfully');
        this.router.navigate(['/status']);
      },
      error: (err) => this.portalState.setError(err.error ?? err.message),
      complete: () => (this.submitting = false)
    });
  }
}
