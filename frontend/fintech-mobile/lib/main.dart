import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'features/auth/login_screen.dart';
import 'features/dashboard/dashboard_screen.dart';
import 'features/onboarding/onboarding_screen.dart';
import 'features/status/application_status_screen.dart';
import 'services/session_controller.dart';
import 'theme.dart';

void main() {
  runApp(const ProviderScope(child: FintechApp()));
}

class FintechApp extends ConsumerWidget {
  const FintechApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final session = ref.watch(sessionProvider);
    return MaterialApp(
      title: 'Fintech App',
      theme: buildBrandTheme(),
      routes: {
        OnboardingScreen.route: (_) => const OnboardingScreen(),
        LoginScreen.route: (_) => const LoginScreen(),
        DashboardScreen.route: (_) => const DashboardScreen(),
        ApplicationStatusScreen.route: (_) => const ApplicationStatusScreen(),
      },
      home: Builder(
        builder: (_) {
          if (!session.isAuthenticated) {
            return const LoginScreen();
          }
          return const DashboardScreen();
        },
      ),
    );
  }
}
