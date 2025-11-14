import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../services/api_client.dart';
import '../../services/session_controller.dart';
import '../dashboard/dashboard_screen.dart';
import '../onboarding/onboarding_screen.dart';
import '../../widgets/brand_shell.dart';

class LoginScreen extends ConsumerStatefulWidget {
  const LoginScreen({super.key});

  static const route = '/login';

  @override
  ConsumerState<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends ConsumerState<LoginScreen> {
  final _formKey = GlobalKey<FormState>();
  final _username = TextEditingController();
  final _password = TextEditingController();
  final _code = TextEditingController();
  bool _awaitingCode = false;
  bool _loading = false;

  @override
  Widget build(BuildContext context) {
    return BrandShell(
      title: 'Welcome back',
      subtitle: 'Sign in to see your Halifax-style account snapshot.',
      child: Form(
        key: _formKey,
        child: Column(
          children: [
            TextFormField(
              controller: _username,
              decoration: const InputDecoration(labelText: 'Username'),
              validator: (value) =>
                  value == null || value.isEmpty ? 'Username required' : null,
            ),
            TextFormField(
              controller: _password,
              decoration: const InputDecoration(labelText: 'Password'),
              obscureText: true,
              validator: (value) =>
                  value == null || value.isEmpty ? 'Password required' : null,
            ),
            if (_awaitingCode)
              TextFormField(
                controller: _code,
                decoration: const InputDecoration(labelText: 'Security code'),
              ),
            const SizedBox(height: 20),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: _loading ? null : _handleSubmit,
                child: _loading
                    ? const CircularProgressIndicator.adaptive(backgroundColor: Colors.white)
                    : Text(_awaitingCode ? 'Verify code' : 'Send code'),
              ),
            ),
            const SizedBox(height: 12),
            TextButton(
              onPressed: () => Navigator.pushNamed(context, OnboardingScreen.route),
              child: const Text('New to us? Start onboarding'),
            )
          ],
        ),
      ),
    );
  }

  Future<void> _handleSubmit() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() => _loading = true);
    final client = ApiClient();
    try {
      if (!_awaitingCode) {
        await client.beginLogin({
          'username': _username.text.trim(),
          'password': _password.text,
        });
        if (mounted) {
          setState(() {
            _awaitingCode = true;
          });
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Security code sent to your email')),
          );
        }
      } else {
        final response = await client.verifyLogin({
          'username': _username.text.trim(),
          'code': _code.text.trim(),
        });
        final token = response['token'] as String;
        final customerId = response['customerId'] as int;
        ref.read(sessionProvider.notifier).setSession(token, customerId);
        if (mounted) {
            Navigator.pushReplacementNamed(context, DashboardScreen.route);
        }
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Login failed: $e')),
        );
      }
    } finally {
      if (mounted) {
        setState(() => _loading = false);
      }
    }
  }
}
