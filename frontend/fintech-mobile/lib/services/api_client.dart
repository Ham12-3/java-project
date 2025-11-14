import 'dart:convert';
import 'package:http/http.dart' as http;

const String apiBaseUrl = String.fromEnvironment(
  'API_BASE_URL',
  defaultValue: 'http://10.0.2.2:8085',
);

class ApiClient {
  final http.Client _client;
  final String? token;

  ApiClient({http.Client? client, this.token}) : _client = client ?? http.Client();

  Map<String, String> _headers() {
    final headers = {'Content-Type': 'application/json'};
    if (token != null) {
      headers['Authorization'] = 'Bearer $token';
    }
    return headers;
  }

  Future<Map<String, dynamic>> signup(Map<String, dynamic> payload) async {
    final response = await _client.post(
      Uri.parse('$apiBaseUrl/api/app/signup'),
      headers: _headers(),
      body: jsonEncode(payload),
    );
    _ensureSuccess(response);
    return jsonDecode(response.body) as Map<String, dynamic>;
  }

  Future<Map<String, dynamic>> beginLogin(Map<String, dynamic> payload) async {
    final response = await _client.post(
      Uri.parse('$apiBaseUrl/api/app/auth/login'),
      headers: _headers(),
      body: jsonEncode(payload),
    );
    _ensureSuccess(response);
    return jsonDecode(response.body) as Map<String, dynamic>;
  }

  Future<Map<String, dynamic>> verifyLogin(Map<String, dynamic> payload) async {
    final response = await _client.post(
      Uri.parse('$apiBaseUrl/api/app/auth/login/verify'),
      headers: _headers(),
      body: jsonEncode(payload),
    );
    _ensureSuccess(response);
    return jsonDecode(response.body) as Map<String, dynamic>;
  }

  Future<Map<String, dynamic>> fetchStatus(int customerId) async {
    final response = await _client.get(
      Uri.parse('$apiBaseUrl/api/app/customers/$customerId/status'),
      headers: _headers(),
    );
    _ensureSuccess(response);
    return jsonDecode(response.body) as Map<String, dynamic>;
  }

  Future<Map<String, dynamic>> fetchAccount(int customerId) async {
    final response = await _client.get(
      Uri.parse('$apiBaseUrl/api/app/customers/$customerId/account'),
      headers: _headers(),
    );
    _ensureSuccess(response);
    return jsonDecode(response.body) as Map<String, dynamic>;
  }

  Future<List<dynamic>> fetchTransactions(int customerId) async {
    final response = await _client.get(
      Uri.parse('$apiBaseUrl/api/app/customers/$customerId/transactions'),
      headers: _headers(),
    );
    _ensureSuccess(response);
    return jsonDecode(response.body) as List<dynamic>;
  }

  Future<Map<String, dynamic>> send(int customerId, Map<String, dynamic> payload) async {
    final response = await _client.post(
      Uri.parse('$apiBaseUrl/api/app/customers/$customerId/send'),
      headers: _headers(),
      body: jsonEncode(payload),
    );
    _ensureSuccess(response);
    return jsonDecode(response.body) as Map<String, dynamic>;
  }

  Future<Map<String, dynamic>> receive(int customerId, Map<String, dynamic> payload) async {
    final response = await _client.post(
      Uri.parse('$apiBaseUrl/api/app/customers/$customerId/receive'),
      headers: _headers(),
      body: jsonEncode(payload),
    );
    _ensureSuccess(response);
    return jsonDecode(response.body) as Map<String, dynamic>;
  }

  void _ensureSuccess(http.Response response) {
    if (response.statusCode >= 200 && response.statusCode < 300) {
      return;
    }
    throw Exception('Request failed: ${response.statusCode} ${response.body}');
  }
}
