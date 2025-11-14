import 'dart:convert';
import 'dart:io';

import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import '../../services/api_client.dart';
import '../../widgets/brand_shell.dart';

class OnboardingScreen extends StatefulWidget {
  const OnboardingScreen({super.key});

  static const route = '/onboarding';

  @override
  State<OnboardingScreen> createState() => _OnboardingScreenState();
}

class _OnboardingScreenState extends State<OnboardingScreen> {
  final _formKey = GlobalKey<FormState>();
  final _fullName = TextEditingController();
  final _username = TextEditingController();
  final _email = TextEditingController();
  final _password = TextEditingController();
  final _company = TextEditingController();
  final _country = TextEditingController();
  final _industry = TextEditingController();
  final _notes = TextEditingController();
  bool _submitting = false;
  List<_DocumentAttachment> _documents = [];

  @override
  Widget build(BuildContext context) {
    return BrandShell(
      title: 'Start onboarding',
      subtitle: 'Open a GBP current account in minutes.',
      child: Form(
        key: _formKey,
        child: Column(
          children: [
            _buildTextField(_fullName, 'Full name', validator: _required),
            _buildTextField(_username, 'Username', validator: _required),
            _buildTextField(_email, 'Email',
                keyboardType: TextInputType.emailAddress, validator: _required),
            _buildTextField(_password, 'Password',
                obscure: true,
                validator: (value) =>
                    value != null && value.length >= 8 ? null : 'Min 8 characters'),
            _buildTextField(_company, 'Company name'),
            Row(
              children: [
                Expanded(child: _buildTextField(_country, 'Country')),
                const SizedBox(width: 12),
                Expanded(child: _buildTextField(_industry, 'Industry')),
              ],
            ),
            _buildTextField(_notes, 'Notes', maxLines: 3),
            const SizedBox(height: 12),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text('KYC documents', style: TextStyle(fontWeight: FontWeight.w600)),
                OutlinedButton.icon(
                  onPressed: _pickDocument,
                  icon: const Icon(Icons.add),
                  label: const Text('Attach'),
                )
              ],
            ),
            const SizedBox(height: 8),
            if (_documents.isEmpty)
              const Align(
                alignment: Alignment.centerLeft,
                child: Text('No files yet', style: TextStyle(color: Colors.grey)),
              )
            else
              ..._documents.map(
                (doc) => Chip(
                  label: Text(doc.name),
                  deleteIcon: const Icon(Icons.close),
                  onDeleted: () => setState(() {
                    _documents = List.of(_documents)..remove(doc);
                  }),
                ),
              ),
            const SizedBox(height: 20),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: _submitting ? null : _submit,
                child: _submitting
                    ? const CircularProgressIndicator.adaptive(backgroundColor: Colors.white)
                    : const Text('Submit application'),
              ),
            )
          ],
        ),
      ),
    );
  }

  Future<void> _pickDocument() async {
    final result = await FilePicker.platform.pickFiles(withData: true);
    if (result == null || result.files.isEmpty) return;
    final file = result.files.first;
    final data = file.bytes ?? await File(file.path!).readAsBytes();
    setState(() {
      _documents = List.of(_documents)
        ..add(_DocumentAttachment(
          name: file.name,
          contentType: file.extension,
          base64Data: base64Encode(data),
        ));
    });
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() => _submitting = true);
    try {
      final payload = {
        'username': _username.text.trim(),
        'email': _email.text.trim(),
        'password': _password.text,
        'fullName': _fullName.text.trim(),
        'companyName': _company.text.trim(),
        'country': _country.text.trim(),
        'industry': _industry.text.trim(),
        'notes': _notes.text.trim(),
        'documents': _documents.map((doc) => doc.toJson()).toList(),
      };
      await ApiClient().signup(payload);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Application submitted! Check status after approval.')),
        );
        Navigator.pop(context);
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Failed: $e')),
        );
      }
    } finally {
      if (mounted) {
        setState(() => _submitting = false);
      }
    }
  }

  String? _required(String? value) =>
      value == null || value.isEmpty ? 'Required' : null;

  Widget _buildTextField(
    TextEditingController controller,
    String label, {
    String? Function(String?)? validator,
    TextInputType? keyboardType,
    bool obscure = false,
    int maxLines = 1,
  }) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12.0),
      child: TextFormField(
        controller: controller,
        validator: validator,
        keyboardType: keyboardType,
        obscureText: obscure,
        maxLines: maxLines,
        decoration: InputDecoration(labelText: label),
      ),
    );
  }
}

class _DocumentAttachment {
  final String name;
  final String? contentType;
  final String base64Data;

  _DocumentAttachment({
    required this.name,
    required this.contentType,
    required this.base64Data,
  });

  Map<String, dynamic> toJson() => {
        'name': name,
        'contentType': contentType,
        'base64Data': base64Data,
      };
}
