import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import '../models/alert.dart';

class ApiService {
  static const _baseUrl = 'https://byd-server-production.up.railway.app';

  // ─── 페어링 ───────────────────────────────────────────────────────────────

  /// 차량 앱의 6자리 코드로 페어링 확인 + 토큰 등록
  static Future<void> confirmPairing({
    required String code,
    required String token,
    required String platform,
    bool sandbox = false,
  }) async {
    final res = await http.post(
      Uri.parse('$_baseUrl/pair/confirm'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'code': code,
        'token': token,
        'platform': platform,
        'sandbox': sandbox ? '1' : '0',
      }),
    );

    if (res.statusCode != 200) {
      final body = jsonDecode(res.body);
      throw Exception(body['error'] ?? '페어링 실패 (HTTP ${res.statusCode})');
    }

    // vehicleId를 로컬에 저장
    final body = jsonDecode(res.body) as Map<String, dynamic>;
    final vehicleId = body['vehicleId'] as String?;
    final accessToken = body['accessToken'] as String?;
    if (vehicleId == null || vehicleId.isEmpty) {
      throw Exception('페어링 응답에 vehicleId가 없습니다');
    }
    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('페어링 응답에 accessToken이 없습니다');
    }

    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('vehicle_id', vehicleId);
    await prefs.setString('access_token', accessToken);
    await prefs.setBool('paired', true);
  }

  /// 페어링 완료 여부 확인
  static Future<bool> isPaired() async {
    final prefs = await SharedPreferences.getInstance();
    final paired = prefs.getBool('paired') ?? false;
    final vehicleId = prefs.getString('vehicle_id');
    final accessToken = prefs.getString('access_token');
    return paired &&
        vehicleId != null &&
        vehicleId.isNotEmpty &&
        accessToken != null &&
        accessToken.isNotEmpty;
  }

  /// 페어링 해제
  static Future<void> unpair(String token) async {
    await unregisterToken(token);
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('vehicle_id');
    await prefs.remove('access_token');
    await prefs.remove('paired');
  }

  // ─── 토큰 ─────────────────────────────────────────────────────────────────

  /// FCM/APNS 토큰을 페어링 인증으로 서버에 등록
  static Future<void> registerToken(
    String token,
    String platform, {
    bool sandbox = false,
  }) async {
    final res = await http.post(
      Uri.parse('$_baseUrl/api/register'),
      headers: await _authorizedHeaders(),
      body: jsonEncode({
        'token': token,
        'platform': platform,
        'sandbox': sandbox ? '1' : '0',
      }),
    );
    if (res.statusCode != 200) {
      throw Exception('registerToken failed: ${res.statusCode}');
    }
  }

  static Future<String?> _getAccessToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString('access_token');
  }

  static Future<Map<String, String>> _authorizedHeaders() async {
    final accessToken = await _getAccessToken();
    if (accessToken == null || accessToken.isEmpty) {
      throw Exception('차량 연결 인증 정보가 없습니다');
    }
    return {
      'Authorization': 'Bearer $accessToken',
      'Content-Type': 'application/json',
    };
  }

  /// 토큰 해제
  static Future<void> unregisterToken(String token) async {
    await http.delete(
      Uri.parse('$_baseUrl/api/unregister'),
      headers: await _authorizedHeaders(),
      body: jsonEncode({'token': token}),
    );
  }

  // ─── 알림 이력 ───────────────────────────────────────────────────────────

  /// 알림 이력 조회
  static Future<List<Alert>> getAlerts({int limit = 50, int offset = 0}) async {
    if (!await isPaired()) {
      throw Exception('차량 연결 정보가 없습니다');
    }

    final uri = Uri.parse('$_baseUrl/alerts').replace(
      queryParameters: {
        'limit': '$limit',
        'offset': '$offset',
      },
    );
    final res = await http.get(uri, headers: await _authorizedHeaders());
    if (res.statusCode != 200) {
      throw Exception('getAlerts failed: ${res.statusCode}');
    }
    final body = jsonDecode(res.body) as Map<String, dynamic>;
    final list = body['alerts'] as List<dynamic>;
    return list.map((e) => Alert.fromJson(e as Map<String, dynamic>)).toList();
  }
}
