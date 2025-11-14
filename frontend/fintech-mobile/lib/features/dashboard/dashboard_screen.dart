import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../services/api_client.dart';
import '../../services/session_controller.dart';
import '../status/application_status_screen.dart';
import '../auth/login_screen.dart';
import '../../theme.dart';

class DashboardScreen extends ConsumerStatefulWidget {
  const DashboardScreen({super.key});

  static const route = '/dashboard';

  @override
  ConsumerState<DashboardScreen> createState() => _DashboardScreenState();
}

class _DashboardScreenState extends ConsumerState<DashboardScreen> {
  Map<String, dynamic>? _account;
  List<dynamic> _transactions = [];
  bool _loading = true;

  final _sendAmount = TextEditingController();
  final _receiveAmount = TextEditingController();

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
    final client = ApiClient(token: session.token);
    try {
      final account = await client.fetchAccount(session.customerId!);
      final txns = await client.fetchTransactions(session.customerId!);
      if (mounted) {
        setState(() {
          _account = account;
          _transactions = txns;
          _loading = false;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() => _loading = false);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Failed to load data: $e')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final session = ref.watch(sessionProvider);
    return Scaffold(
      body: RefreshIndicator(
        onRefresh: _load,
        child: CustomScrollView(
          slivers: [
            SliverAppBar(
              floating: true,
              snap: true,
              title: const Text('Your Halifax-style account'),
              actions: [
                IconButton(
                  icon: const Icon(Icons.logout),
                  onPressed: () {
                    ref.read(sessionProvider.notifier).clear();
                    Navigator.pushNamedAndRemoveUntil(context, LoginScreen.route, (_) => false);
                  },
                )
              ],
            ),
            SliverToBoxAdapter(
              child: _loading
                  ? const Padding(
                      padding: EdgeInsets.only(top: 80),
                      child: Center(child: CircularProgressIndicator()),
                    )
                  : Padding(
                      padding: const EdgeInsets.all(16),
                      child: Column(
                        children: [
                          _AccountHero(account: _account),
                          const SizedBox(height: 16),
                          _quickActions(session),
                          const SizedBox(height: 16),
                          _transferForm(
                            title: 'Send money',
                            controller: _sendAmount,
                            action: () => _submitTransfer(session.customerId!, true),
                          ),
                          const SizedBox(height: 12),
                          _transferForm(
                            title: 'Receive money',
                            controller: _receiveAmount,
                            action: () => _submitTransfer(session.customerId!, false),
                          ),
                          const SizedBox(height: 16),
                          ListTile(
                            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                            tileColor: Colors.white,
                            title: const Text('Application status'),
                            subtitle: const Text('Track verification progress'),
                            trailing: const Icon(Icons.chevron_right),
                            onTap: () =>
                                Navigator.pushNamed(context, ApplicationStatusScreen.route),
                          ),
                          const SizedBox(height: 20),
                          Align(
                            alignment: Alignment.centerLeft,
                            child: Text('Recent transactions',
                                style: Theme.of(context).textTheme.titleMedium),
                          ),
                          const SizedBox(height: 8),
                          if (_transactions.isEmpty)
                            const Card(
                              child: Padding(
                                padding: EdgeInsets.all(16.0),
                                child: Text('No activity yet.'),
                              ),
                            )
                          else
                            ..._transactions.take(10).map((txn) => _TransactionTile(txn: txn)),
                        ],
                      ),
                    ),
            )
          ],
        ),
      ),
    );
  }

  Widget _transferForm({
    required String title,
    required TextEditingController controller,
    required VoidCallback action,
  }) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(title, style: const TextStyle(fontWeight: FontWeight.bold)),
                const SizedBox(height: 12),
                TextField(
                  controller: controller,
                  keyboardType: const TextInputType.numberWithOptions(decimal: true),
                  decoration: const InputDecoration(
                    labelText: 'Amount (£)',
                    hintText: '0.00',
                  ),
                ),
                const SizedBox(height: 12),
                SizedBox(
                  width: double.infinity,
                  child: ElevatedButton(onPressed: action, child: const Text('Submit request')),
                ),
              ],
            ),
          ),
    );
  }

  Future<void> _submitTransfer(int customerId, bool send) async {
    final amount = double.tryParse(send ? _sendAmount.text : _receiveAmount.text);
    if (amount == null || amount <= 0) {
      ScaffoldMessenger.of(context)
          .showSnackBar(const SnackBar(content: Text('Enter a valid amount.')));
      return;
    }
    final client = ApiClient(token: ref.read(sessionProvider).token);
    try {
      final payload = {'amount': amount};
      final response =
          send ? await client.send(customerId, payload) : await client.receive(customerId, payload);
      if (mounted) {
        setState(() {
          _account = response;
        });
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(send ? 'Sent!' : 'Received!')),
        );
      }
      await _load();
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Transfer failed: $e')),
      );
    }
  }
}

class _AccountHero extends StatelessWidget {
  const _AccountHero({required this.account});

  final Map<String, dynamic>? account;

  @override
  Widget build(BuildContext context) {
    if (account == null || account!['provisioned'] != true) {
      return Container(
        decoration: BoxDecoration(
          gradient: const LinearGradient(
            colors: [BrandColors.primary, BrandColors.secondary],
          ),
          borderRadius: BorderRadius.circular(22),
        ),
        padding: const EdgeInsets.all(20),
        child: const Text(
          'Your account will be provisioned once operations approves your onboarding.',
          style: TextStyle(color: Colors.white),
        ),
      );
    }
    double parseAmount(String key) {
      final value = account![key];
      if (value is num) return value.toDouble();
      if (value is String) return double.tryParse(value) ?? 0;
      return 0;
    }

    final balance = parseAmount('balance');
    final available = parseAmount('availableBalance');

    return Container(
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          colors: [BrandColors.primary, BrandColors.secondary],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(22),
      ),
      padding: const EdgeInsets.all(24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            '£${balance.toStringAsFixed(2)}',
            style: Theme.of(context)
                .textTheme
                .headlineMedium
                ?.copyWith(color: Colors.white, fontWeight: FontWeight.bold),
          ),
          Text(
            'Available £${available.toStringAsFixed(2)}',
            style: const TextStyle(color: Colors.white70),
          ),
          const SizedBox(height: 12),
          Text('Account ${account!['accountNumber']}', style: const TextStyle(color: Colors.white)),
          Text('Sort code ${account!['sortCode']}', style: const TextStyle(color: Colors.white70)),
        ],
      ),
    );
  }
}

Widget _quickActions(SessionState session) {
  final items = [
    {'label': 'Top up', 'icon': Icons.add_card},
    {'label': 'Statements', 'icon': Icons.description_outlined},
    {'label': 'Insights', 'icon': Icons.show_chart},
  ];
  return Row(
    children: items
        .map(
          (item) => Expanded(
            child: Container(
              margin: const EdgeInsets.symmetric(horizontal: 4),
              padding: const EdgeInsets.symmetric(vertical: 14),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(16),
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.05),
                    blurRadius: 12,
                    offset: const Offset(0, 6),
                  )
                ],
              ),
              child: Column(
                children: [
                  Icon(item['icon'] as IconData, color: BrandColors.primary),
                  const SizedBox(height: 8),
                  Text(item['label'] as String,
                      style: const TextStyle(fontWeight: FontWeight.w600)),
                ],
              ),
            ),
          ),
        )
        .toList(),
  );
}

class _TransactionTile extends StatelessWidget {
  const _TransactionTile({required this.txn});

  final Map<String, dynamic> txn;

  @override
  Widget build(BuildContext context) {
    final isCredit = txn['type'] == 'CREDIT';
    final amount = txn['amount'];
    return Card(
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: isCredit ? Colors.green.shade100 : Colors.red.shade100,
          child: Icon(
            isCredit ? Icons.arrow_downward : Icons.arrow_upward,
            color: isCredit ? Colors.green.shade700 : Colors.red.shade700,
          ),
        ),
        title: Text(txn['counterparty'] ?? 'Transfer'),
        subtitle: Text(txn['createdAt'] ?? ''),
        trailing: Text(
          '${isCredit ? '+' : '-'}£$amount',
          style: TextStyle(
            color: isCredit ? Colors.green.shade700 : Colors.red.shade700,
            fontWeight: FontWeight.bold,
          ),
        ),
      ),
    );
  }
}
