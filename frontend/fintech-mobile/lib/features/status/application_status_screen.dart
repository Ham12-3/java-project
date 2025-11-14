import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../services/api_client.dart';
import '../../services/session_controller.dart';
import '../../theme.dart';

class ApplicationStatusScreen extends ConsumerStatefulWidget {
  const ApplicationStatusScreen({super.key});

  static const route = '/status';

  @override
  ConsumerState<ApplicationStatusScreen> createState() => _ApplicationStatusScreenState();
}

class _ApplicationStatusScreenState extends ConsumerState<ApplicationStatusScreen> {
  Map<String, dynamic>? _status;
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    final session = ref.read(sessionProvider);
    if (!session.isAuthenticated) {
      setState(() => _loading = false);
      return;
    }
    try {
      final response = await ApiClient(token: session.token).fetchStatus(session.customerId!);
      if (mounted) {
        setState(() {
          _status = response;
          _loading = false;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() => _loading = false);
        ScaffoldMessenger.of(context)
            .showSnackBar(SnackBar(content: Text('Unable to fetch status: $e')));
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Application status')),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : _status == null
              ? const Center(child: Text('No active session.'))
              : Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    children: [
                      _statusCard(
                        title: 'Onboarding',
                        value: _status!['onboardingStatus'],
                        icon: Icons.assignment_turned_in,
                      ),
                      const SizedBox(height: 12),
                      _statusCard(
                        title: 'Verification',
                        value: _status!['verificationStatus'],
                        icon: Icons.verified_user,
                      ),
                      const SizedBox(height: 12),
                      _statusCard(
                        title: 'Account',
                        value: _status!['account']?['provisioned'] == true
                            ? 'Ready'
                            : 'Pending approval',
                        icon: Icons.account_balance_wallet,
                      ),
                    ],
                  ),
                ),
    );
  }

  Widget _statusCard({required String title, required String value, required IconData icon}) {
    final ready = value.toLowerCase().contains('ready') ||
        value.toLowerCase().contains('approved');
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(18),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(18),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.05),
            blurRadius: 10,
            offset: const Offset(0, 6),
          )
        ],
      ),
      child: Row(
        children: [
          CircleAvatar(
            backgroundColor: ready ? Colors.green.shade50 : Colors.amber.shade50,
            child: Icon(icon, color: ready ? Colors.green : Colors.amber.shade800),
          ),
          const SizedBox(width: 14),
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(title, style: const TextStyle(fontWeight: FontWeight.w600)),
              Text(value, style: const TextStyle(color: Colors.black54)),
            ],
          )
        ],
      ),
    );
  }
}
