import 'package:flutter_riverpod/flutter_riverpod.dart';

class SessionState {
  final String? token;
  final int? customerId;

  const SessionState({this.token, this.customerId});

  bool get isAuthenticated => token != null && customerId != null;

  SessionState copyWith({String? token, int? customerId}) {
    return SessionState(
      token: token ?? this.token,
      customerId: customerId ?? this.customerId,
    );
  }
}

class SessionController extends StateNotifier<SessionState> {
  SessionController() : super(const SessionState());

  void setSession(String token, int customerId) {
    state = SessionState(token: token, customerId: customerId);
  }

  void clear() {
    state = const SessionState();
  }
}

final sessionProvider = StateNotifierProvider<SessionController, SessionState>(
  (ref) => SessionController(),
);
