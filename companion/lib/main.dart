import 'dart:io';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'firebase_options.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'screens/alert_list_screen.dart';
import 'screens/pair_screen.dart';
import 'services/api_service.dart';
import 'services/notification_service.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(options: DefaultFirebaseOptions.currentPlatform);
  await NotificationService.initialize();
  _registerPushTokenIfPaired();
  runApp(const ProviderScope(child: BydHealthMonitorApp()));
}

/// 페어링 완료 기기의 토큰을 서버에 등록/갱신.
/// vehicle_id를 함께 전송하여 서버에서 페어링 연결을 유지한다.
Future<void> _registerPushTokenIfPaired() async {
  final paired = await ApiService.isPaired();
  if (!paired) return;

  try {
    if (Platform.isIOS) {
      await _refreshApnsToken();
    } else {
      await _refreshFcmToken();
      // FCM 토큰 갱신 시 vehicle_id 포함해 재등록
      FirebaseMessaging.instance.onTokenRefresh.listen((newToken) async {
        const isSandbox = bool.fromEnvironment('SANDBOX', defaultValue: false);
        await ApiService.registerToken(newToken, 'android', sandbox: isSandbox);
        // vehicle_id는 registerToken 내부에서 SharedPreferences에서 자동으로 읽음
      });
    }
  } catch (e) {
    debugPrint('[push] token registration failed: $e');
  }
}

/// iOS: APNs 토큰을 확인하고 변경됐으면 vehicle_id와 함께 재등록
Future<void> _refreshApnsToken() async {
  final newToken = await NotificationService.getApnsToken();
  if (newToken == null) return;

  final prefs = await SharedPreferences.getInstance();
  final savedToken = prefs.getString('last_apns_token');
  const isSandbox = bool.fromEnvironment('SANDBOX', defaultValue: true);

  // 토큰이 변경됐거나 처음 등록인 경우에만 서버 호출
  if (newToken != savedToken) {
    await ApiService.registerToken(newToken, 'ios', sandbox: isSandbox);
    await prefs.setString('last_apns_token', newToken);
    debugPrint('[push] APNs token refreshed and re-registered');
  }
}

/// Android: FCM 토큰 확인 및 vehicle_id와 함께 등록
Future<void> _refreshFcmToken() async {
  final newToken = await NotificationService.getFcmToken();
  if (newToken == null) return;

  final prefs = await SharedPreferences.getInstance();
  final savedToken = prefs.getString('last_fcm_token');

  if (newToken != savedToken) {
    await ApiService.registerToken(newToken, 'android');
    await prefs.setString('last_fcm_token', newToken);
    debugPrint('[push] FCM token refreshed and re-registered');
  }
}

class BydHealthMonitorApp extends StatelessWidget {
  const BydHealthMonitorApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'BYD Health Monitor',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: const ColorScheme.dark(
          primary:   Color(0xFF1A8CFF),
          surface:   Color(0xFF0E0E10),
          onSurface: Colors.white,
        ),
        scaffoldBackgroundColor: const Color(0xFF0A0E14),
        useMaterial3: true,
      ),
      home: const _RootScreen(),
    );
  }
}

class _RootScreen extends StatefulWidget {
  const _RootScreen();

  @override
  State<_RootScreen> createState() => _RootScreenState();
}

class _RootScreenState extends State<_RootScreen> {
  late Future<bool> _pairedFuture;

  @override
  void initState() {
    super.initState();
    _pairedFuture = ApiService.isPaired();
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<bool>(
      future: _pairedFuture,
      builder: (context, snap) {
        if (!snap.hasData) {
          return const Scaffold(
            backgroundColor: Color(0xFF0A0E14),
            body: Center(child: CircularProgressIndicator(color: Color(0xFF1A8CFF))),
          );
        }

        if (snap.data!) {
          return const AlertListScreen();
        }

        return PairScreen(
          onPaired: () => setState(() {
            _pairedFuture = Future.value(true);
          }),
        );
      },
    );
  }
}
