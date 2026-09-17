class Alert {
  final int id;
  final int code;
  final String severity;
  final String name;
  final String description;
  final String? vin;
  final DateTime alertedAt;

  const Alert({
    required this.id,
    required this.code,
    required this.severity,
    required this.name,
    required this.description,
    this.vin,
    required this.alertedAt,
  });

  factory Alert.fromJson(Map<String, dynamic> json) => Alert(
        id:          json['id'] as int,
        code:        json['code'] as int,
        severity:    json['severity'] as String,
        name:        json['name'] as String,
        description: json['description'] as String,
        vin:         json['vin'] as String?,
        alertedAt:   DateTime.parse(json['alerted_at'] as String).toLocal(),
      );

  bool get isCritical => severity == 'CRITICAL';
  bool get isWarning  => severity == 'WARNING';
}
