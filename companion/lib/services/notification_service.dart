import 'dart:io';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';

/// 백그라운드 FCM 메시지 핸들러 (top-level 함수 필수)
@pragma('vm:entry-point')
Future<void> firebaseMessagingBackgroundHandler(RemoteMessage message) async {
  await NotificationService.showLocalNotification(message);
}

class NotificationService {
  static final _localNotifications = FlutterLocalNotificationsPlugin();

  static const _channelId   = 'byd_fault_alerts';
  static const _channelName = 'BYD 고장 알림';
  static const _channelDesc = '차량 CRITICAL 고장 감지 시 발송되는 알림';

  static Future<void> initialize() async {
    // 로컬 알림 초기화
    const androidSettings = AndroidInitializationSettings('@mipmap/ic_launcher');
    const iosSettings     = DarwinInitializationSettings(
      requestAlertPermission: true,
      requestBadgePermission: true,
      requestSoundPermission: true,
    );
    await _localNotifications.initialize(
      const InitializationSettings(android: androidSettings, iOS: iosSettings),
    );

    // Android 알림 채널 생성
    if (Platform.isAndroid) {
      await _localNotifications
          .resolvePlatformSpecificImplementation<
              AndroidFlutterLocalNotificationsPlugin>()
          ?.createNotificationChannel(
            const AndroidNotificationChannel(
              _channelId,
              _channelName,
              description: _channelDesc,
              importance: Importance.max,
            ),
          );
    }

    // FCM 권한 요청
    await FirebaseMessaging.instance.requestPermission(
      alert: true, badge: true, sound: true,
    );

    // 포그라운드 메시지 수신
    FirebaseMessaging.onMessage.listen(showLocalNotification);

    // 백그라운드 핸들러 등록
    FirebaseMessaging.onBackgroundMessage(firebaseMessagingBackgroundHandler);
  }

  static Future<void> showLocalNotification(RemoteMessage message) async {
    final notification = message.notification;
    if (notification == null) return;

    await _localNotifications.show(
      message.hashCode,
      notification.title,
      notification.body,
      NotificationDetails(
        android: AndroidNotificationDetails(
          _channelId,
          _channelName,
          channelDescription: _channelDesc,
          importance: Importance.max,
          priority:   Priority.high,
          icon:       '@mipmap/ic_launcher',
        ),
        iOS: const DarwinNotificationDetails(
          presentAlert: true,
          presentBadge: true,
          presentSound: true,
        ),
      ),
    );
  }

  /// FCM 토큰 반환 (서버 등록용)
  static Future<String?> getFcmToken() =>
      FirebaseMessaging.instance.getToken();

  /// APNS 토큰 반환 (iOS 서버 등록용)
  static Future<String?> getApnsToken() =>
      FirebaseMessaging.instance.getAPNSToken();
}
