import 'dart:io';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
// flutterfire configure 실행 후 자동 생성됨
// import 'firebase_options.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'screens/alert_list_screen.dart';
import 'services/api_service.dart';
import 'services/notification_service.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Firebase 초기화
  // flutterfire configure 실행 후 아래 주석 해제
  // await Firebase.initializeApp(options: DefaultFirebaseOptions.currentPlatform);
  await Firebase.initializeApp();

  // 알림 서비스 초기화 + 백그라운드 핸들러 등록
  await NotificationService.initialize();

  // 서버에 FCM/APNS 토큰 등록
  await _registerPushToken();

  runApp(const ProviderScope(child: BydHealthMonitorApp()));
}

Future<void> _registerPushToken() async {
  try {
    if (Platform.isIOS) {
      final apnsToken = await NotificationService.getApnsToken();
      if (apnsToken != null) {
        // iOS: APNS 토큰으로 서버 등록
        // sandbox 여부는 빌드 환경에 따라 결정 (Debug = sandbox)
        const isSandbox = bool.fromEnvironment('SANDBOX', defaultValue: true);
        await ApiService.registerToken(apnsToken, 'ios', sandbox: isSandbox);
      }
    } else {
      final fcmToken = await NotificationService.getFcmToken();
      if (fcmToken != null) {
        await ApiService.registerToken(fcmToken, 'android');
      }
      // FCM 토큰 갱신 시 재등록
      FirebaseMessaging.instance.onTokenRefresh.listen((token) {
        ApiService.registerToken(token, 'android');
      });
    }
  } catch (e) {
    // 토큰 등록 실패는 앱 실행을 막지 않음
    debugPrint('[push] token registration failed: $e');
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
          primary:    Color(0xFF1A8CFF),
          surface:    Color(0xFF0E0E10),
          onSurface:  Colors.white,
        ),
        scaffoldBackgroundColor: const Color(0xFF0A0E14),
        useMaterial3: true,
      ),
      home: const AlertListScreen(),
    );
  }
}
