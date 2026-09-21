import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../services/api_service.dart';
import '../services/notification_service.dart';

class PairScreen extends StatefulWidget {
  final VoidCallback onPaired;
  const PairScreen({super.key, required this.onPaired});

  @override
  State<PairScreen> createState() => _PairScreenState();
}

class _PairScreenState extends State<PairScreen> {
  final _ctrl = TextEditingController();
  bool _loading = false;
  String? _error;

  @override
  void dispose() {
    _ctrl.dispose();
    super.dispose();
  }

  Future<void> _confirm() async {
    final code = _ctrl.text.trim().replaceAll(' ', '');
    if (code.length != 6) {
      setState(() => _error = '6자리 코드를 입력하세요');
      return;
    }

    setState(() { _loading = true; _error = null; });

    try {
      String? token;
      String platform;
      bool sandbox = false;

      if (Platform.isIOS) {
        token = await NotificationService.getApnsToken();
        platform = 'ios';
        const isSandbox = bool.fromEnvironment('SANDBOX', defaultValue: true);
        sandbox = isSandbox;
      } else {
        token = await NotificationService.getFcmToken();
        platform = 'android';
      }

      if (token == null) {
        setState(() { _loading = false; _error = '푸시 토큰을 가져올 수 없습니다. 알림 권한을 확인하세요.'; });
        return;
      }

      await ApiService.confirmPairing(
        code: code,
        token: token,
        platform: platform,
        sandbox: sandbox,
      );

      widget.onPaired();
    } catch (e) {
      setState(() { _error = e.toString().replaceFirst('Exception: ', ''); });
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF0A0E14),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 28, vertical: 40),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text(
                'BYD Health Monitor',
                style: TextStyle(color: Color(0xFF1A8CFF), fontSize: 14, fontWeight: FontWeight.w600),
              ),
              const SizedBox(height: 12),
              const Text(
                '차량 앱 연결',
                style: TextStyle(color: Colors.white, fontSize: 28, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 8),
              const Text(
                '차량 디스플레이에 표시된\n6자리 코드를 입력하세요',
                style: TextStyle(color: Color(0xFF8A9BB0), fontSize: 15, height: 1.5),
              ),
              const SizedBox(height: 48),

              // 코드 입력 필드
              TextField(
                controller: _ctrl,
                keyboardType: TextInputType.number,
                maxLength: 7,
                textAlign: TextAlign.center,
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 36,
                  fontWeight: FontWeight.bold,
                  letterSpacing: 8,
                ),
                decoration: InputDecoration(
                  counterText: '',
                  hintText: '000 000',
                  hintStyle: TextStyle(color: Colors.white.withValues(alpha: 0.2), letterSpacing: 8, fontSize: 36),
                  filled: true,
                  fillColor: const Color(0xFF131920),
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(14),
                    borderSide: const BorderSide(color: Color(0xFF1E2D3D)),
                  ),
                  enabledBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(14),
                    borderSide: const BorderSide(color: Color(0xFF1E2D3D)),
                  ),
                  focusedBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(14),
                    borderSide: const BorderSide(color: Color(0xFF1A8CFF), width: 2),
                  ),
                  errorText: _error,
                  errorStyle: const TextStyle(color: Color(0xFFFF3B3B), fontSize: 13),
                ),
                inputFormatters: [
                  FilteringTextInputFormatter.digitsOnly,
                  // 3자리 후 자동 공백 삽입
                  _CodeFormatter(),
                ],
                onSubmitted: (_) => _confirm(),
              ),

              const SizedBox(height: 28),

              SizedBox(
                width: double.infinity,
                height: 52,
                child: ElevatedButton(
                  onPressed: _loading ? null : _confirm,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF1A8CFF),
                    disabledBackgroundColor: const Color(0xFF1A8CFF).withValues(alpha: 0.4),
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                  ),
                  child: _loading
                      ? const SizedBox(
                          width: 22, height: 22,
                          child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2.5),
                        )
                      : const Text('연결하기', style: TextStyle(color: Colors.white, fontSize: 16, fontWeight: FontWeight.w600)),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _CodeFormatter extends TextInputFormatter {
  @override
  TextEditingValue formatEditUpdate(TextEditingValue oldValue, TextEditingValue newValue) {
    final digits = newValue.text.replaceAll(' ', '');
    if (digits.length > 6) return oldValue;

    final buffer = StringBuffer();
    for (int i = 0; i < digits.length; i++) {
      if (i == 3) buffer.write(' ');
      buffer.write(digits[i]);
    }
    final formatted = buffer.toString();
    return TextEditingValue(
      text: formatted,
      selection: TextSelection.collapsed(offset: formatted.length),
    );
  }
}
