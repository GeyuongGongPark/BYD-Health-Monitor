import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import '../models/alert.dart';
import '../services/api_service.dart';

final alertsProvider = FutureProvider<List<Alert>>((ref) => ApiService.getAlerts());

class AlertListScreen extends ConsumerWidget {
  const AlertListScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final alertsAsync = ref.watch(alertsProvider);

    return Scaffold(
      backgroundColor: const Color(0xFF0A0E14),
      appBar: AppBar(
        backgroundColor: const Color(0xFF131920),
        title: const Text(
          'BYD Health Monitor',
          style: TextStyle(color: Colors.white, fontWeight: FontWeight.w600),
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh, color: Color(0xFF1A8CFF)),
            onPressed: () => ref.invalidate(alertsProvider),
          ),
        ],
      ),
      body: alertsAsync.when(
        loading: () => const Center(
          child: CircularProgressIndicator(color: Color(0xFF1A8CFF)),
        ),
        error: (e, _) => Center(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              const Icon(Icons.wifi_off, color: Color(0xFF98989F), size: 48),
              const SizedBox(height: 12),
              Text('연결 실패', style: TextStyle(color: Colors.white.withValues(alpha: .7))),
              const SizedBox(height: 8),
              TextButton(
                onPressed: () => ref.invalidate(alertsProvider),
                child: const Text('다시 시도', style: TextStyle(color: Color(0xFF1A8CFF))),
              ),
            ],
          ),
        ),
        data: (alerts) => alerts.isEmpty
            ? const _EmptyState()
            : RefreshIndicator(
                color: const Color(0xFF1A8CFF),
                onRefresh: () async => ref.invalidate(alertsProvider),
                child: ListView.separated(
                  padding: const EdgeInsets.all(16),
                  itemCount: alerts.length,
                  separatorBuilder: (_, __) => const SizedBox(height: 8),
                  itemBuilder: (_, i) => _AlertCard(alert: alerts[i]),
                ),
              ),
      ),
    );
  }
}

class _AlertCard extends StatelessWidget {
  final Alert alert;
  const _AlertCard({required this.alert});

  @override
  Widget build(BuildContext context) {
    final (bgColor, iconColor, icon) = switch (alert.severity) {
      'CRITICAL' => (const Color(0x1AFF3B3B), const Color(0xFFFF3B3B), Icons.error_outline),
      'WARNING'  => (const Color(0x1AFF8C00), const Color(0xFFFF8C00), Icons.warning_amber_outlined),
      _          => (const Color(0x1AFFD600), const Color(0xFFFFD600), Icons.info_outline),
    };

    final label = switch (alert.severity) {
      'CRITICAL' => '긴급',
      'WARNING'  => '주의',
      _          => '정보',
    };

    final dateStr = DateFormat('yyyy.MM.dd HH:mm').format(alert.alertedAt);

    return Container(
      decoration: BoxDecoration(
        color: const Color(0xFF1A2332),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: Colors.white.withValues(alpha: .06)),
      ),
      padding: const EdgeInsets.all(14),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Container(
            width: 40, height: 40,
            decoration: BoxDecoration(color: bgColor, borderRadius: BorderRadius.circular(10)),
            child: Icon(icon, color: iconColor, size: 22),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Text(
                      alert.name,
                      style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w600, fontSize: 14),
                    ),
                    const SizedBox(width: 8),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                      decoration: BoxDecoration(
                        color: bgColor,
                        borderRadius: BorderRadius.circular(4),
                        border: Border.all(color: iconColor.withValues(alpha: .3)),
                      ),
                      child: Text(label, style: TextStyle(color: iconColor, fontSize: 11, fontWeight: FontWeight.w600)),
                    ),
                  ],
                ),
                const SizedBox(height: 4),
                Text(alert.description, style: const TextStyle(color: Color(0xFF98989F), fontSize: 13)),
                const SizedBox(height: 4),
                Text(dateStr, style: const TextStyle(color: Color(0xFF5A5A60), fontSize: 12)),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _EmptyState extends StatelessWidget {
  const _EmptyState();

  @override
  Widget build(BuildContext context) {
    return const Center(
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(Icons.check_circle_outline, color: Color(0xFF00E676), size: 56),
          SizedBox(height: 16),
          Text('알림 이력이 없습니다', style: TextStyle(color: Colors.white, fontSize: 16)),
          SizedBox(height: 8),
          Text('차량에 이상이 감지되면 여기에 표시됩니다', style: TextStyle(color: Color(0xFF98989F), fontSize: 13)),
        ],
      ),
    );
  }
}
