package com.bydhealth.monitor.domain.model

enum class Severity { CRITICAL, WARNING, INFO }

data class MalfunctionInfo(
    val code: Int,
    val name: String,
    val description: String,
    val severity: Severity,
    val action: String,
    val isActive: Boolean = false,
)
