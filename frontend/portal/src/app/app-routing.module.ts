import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { ForgotPasswordComponent } from './pages/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './pages/reset-password/reset-password.component';
import { CustomerOnboardingComponent } from './pages/customer-onboarding/customer-onboarding.component';
import { PipelineComponent } from './pages/pipeline/pipeline.component';
import { StatusComponent } from './pages/status/status.component';
import { CustomersComponent } from './pages/customers/customers.component';
import { CustomerProfileComponent } from './pages/customer-profile/customer-profile.component';
import { OperationsDashboardComponent } from './pages/operations-dashboard/operations-dashboard.component';

const routes: Routes = [
  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },
  { path: 'auth/login', component: LoginComponent },
  { path: 'auth/register', component: RegisterComponent },
  { path: 'auth/forgot-password', component: ForgotPasswordComponent },
  { path: 'auth/reset-password', component: ResetPasswordComponent },
  { path: 'onboarding/customer', component: CustomerOnboardingComponent },
  { path: 'onboarding/pipeline', component: PipelineComponent },
  { path: 'status', component: StatusComponent },
  { path: 'customers', component: CustomersComponent },
  { path: 'customers/:id', component: CustomerProfileComponent },
  { path: 'operations', component: OperationsDashboardComponent },
  { path: '**', redirectTo: 'auth/login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
