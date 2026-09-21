import 'dart:convert';
import 'package:http/http.dart' as http;

class UpdateInfo {
  final String latestVersion;
  final String releaseUrl;

  UpdateInfo({required this.latestVersion, required this.releaseUrl});
}

class UpdateService {
  static const _releasesUrl =
      'https://api.github.com/repos/GeyuongGongPark/BYD-Health-Monitor/releases/latest';

  static Future<UpdateInfo?> checkForUpdate(String currentVersion) async {
    try {
      final res = await http
          .get(
            Uri.parse(_releasesUrl),
            headers: {'Accept': 'application/vnd.github+json'},
          )
          .timeout(const Duration(seconds: 10));

      if (res.statusCode != 200) return null;

      final json = jsonDecode(res.body) as Map<String, dynamic>;
      final tagName = json['tag_name'] as String; // "v1.0.0"
      final latestVersion = tagName.replaceFirst('v', ''); // "1.0.0"
      final releaseUrl = json['html_url'] as String;

      if (!_isNewer(latestVersion, currentVersion)) return null;

      return UpdateInfo(latestVersion: latestVersion, releaseUrl: releaseUrl);
    } catch (_) {
      return null;
    }
  }

  static bool _isNewer(String latest, String current) {
    final l = latest.split('.').map((s) => int.tryParse(s) ?? 0).toList();
    final c = current.split('.').map((s) => int.tryParse(s) ?? 0).toList();
    final maxLen = l.length > c.length ? l.length : c.length;
    for (var i = 0; i < maxLen; i++) {
      final lv = i < l.length ? l[i] : 0;
      final cv = i < c.length ? c[i] : 0;
      if (lv > cv) return true;
      if (lv < cv) return false;
    }
    return false;
  }
}
