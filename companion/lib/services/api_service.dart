import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/alert.dart';

class ApiService {
  static const _baseUrl = 'https://byd-server-production.up.railway.app';
  static const _apiKey  = '04481698d4b2dff5dcc8d39a811c2c501025da86f90f2d52f0b098cbba5fa7f4';

  static final _headers = {
    'Authorization': 'Bearer $_apiKey',
    'Content-Type':  'application/json',
  };

  /// FCM/APNS 토큰을 서버에 등록
  static Future<void> registerToken(String token, String platform, {bool sandbox = false}) async {
    final res = await http.post(
      Uri.parse('$_baseUrl/api/register'),
      headers: _headers,
      body: jsonEncode({'token': token, 'platform': platform, 'sandbox': sandbox ? '1' : '0'}),
    );
    if (res.statusCode != 200) {
      throw Exception('registerToken failed: ${res.statusCode}');
    }
  }

  /// 토큰 해제 (앱 로그아웃/삭제 시)
  static Future<void> unregisterToken(String token) async {
    await http.delete(
      Uri.parse('$_baseUrl/api/unregister'),
      headers: _headers,
      body: jsonEncode({'token': token}),
    );
  }

  /// 알림 이력 조회
  static Future<List<Alert>> getAlerts({int limit = 50, int offset = 0}) async {
    final uri = Uri.parse('$_baseUrl/alerts').replace(
      queryParameters: {'limit': '$limit', 'offset': '$offset'},
    );
    final res = await http.get(uri, headers: _headers);
    if (res.statusCode != 200) {
      throw Exception('getAlerts failed: ${res.statusCode}');
    }
    final body = jsonDecode(res.body) as Map<String, dynamic>;
    final list = body['alerts'] as List<dynamic>;
    return list.map((e) => Alert.fromJson(e as Map<String, dynamic>)).toList();
  }
}
