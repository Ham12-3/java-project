import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
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

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    ForgotPasswordComponent,
    ResetPasswordComponent,
    CustomerOnboardingComponent,
    PipelineComponent,
    StatusComponent,
    CustomersComponent,
    CustomerProfileComponent,
    OperationsDashboardComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {}
