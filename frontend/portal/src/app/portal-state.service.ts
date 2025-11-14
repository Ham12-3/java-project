import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class PortalStateService {
  private readonly tokenSubject = new BehaviorSubject<string>('');
  private readonly errorSubject = new BehaviorSubject<string>('');
  private readonly resetMessageSubject = new BehaviorSubject<string>('');
  private readonly resetTokenSubject = new BehaviorSubject<string>('');
  private readonly toastSubject = new BehaviorSubject<string>('');

  readonly lastToken$ = this.tokenSubject.asObservable();
  readonly lastError$ = this.errorSubject.asObservable();
  readonly resetMessage$ = this.resetMessageSubject.asObservable();
  readonly resetToken$ = this.resetTokenSubject.asObservable();
  readonly toast$ = this.toastSubject.asObservable();

  setToken(token: string) {
    this.tokenSubject.next(token);
  }

  setError(message: string) {
    this.errorSubject.next(message ?? '');
  }

  setResetMessage(message: string) {
    this.resetMessageSubject.next(message ?? '');
  }

  setResetToken(token: string) {
    this.resetTokenSubject.next(token ?? '');
  }

  setToast(message: string) {
    this.toastSubject.next(message ?? '');
    if (message) {
      setTimeout(() => this.toastSubject.next(''), 4000);
    }
  }
}
