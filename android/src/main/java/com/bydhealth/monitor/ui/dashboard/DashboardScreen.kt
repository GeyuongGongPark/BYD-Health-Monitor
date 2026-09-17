package com.bydhealth.monitor.ui.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bydhealth.monitor.domain.maintenance.MaintenanceStatus
import com.bydhealth.monitor.ui.theme.*

@Composable
fun DashboardScreen(
    onMalfunctionClick: () -> Unit,
    onTyreClick: () -> Unit,
    onMaintenanceClick: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(Modifier.fillMaxSize().background(BackgroundDark)) {
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BydBlueBright)
            }
        } else {
            Row(Modifier.fillMaxSize().padding(20.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                // 좌측: 건강 점수 게이지
                Column(
                    Modifier.weight(1f).fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    HealthGauge(score = uiState.healthScore, modifier = Modifier.size(220.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("차량 건강 점수", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    uiState.vehicleHealth?.let { health ->
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "배터리 ${health.elecPercentage.toInt()}%  ·  주행 가능 ${health.elecDrivingRange}km",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                        )
                    }
                }

                // 우측: 섹션 카드들
                Column(
                    Modifier.weight(1f).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically),
                ) {
                    SectionCard(
                        icon = Icons.Default.Warning,
                        title = "고장 현황",
                        subtitle = when {
                            uiState.criticalCount > 0 -> "긴급 ${uiState.criticalCount}건"
                            uiState.warningCount > 0  -> "주의 ${uiState.warningCount}건"
                            else                      -> "모든 시스템 정상"
                        },
                        statusColor = when {
                            uiState.criticalCount > 0 -> CriticalRed
                            uiState.warningCount > 0  -> WarningOrange
                            else                      -> HealthGreen
                        },
                        onClick = onMalfunctionClick,
                    )
                    SectionCard(
                        icon = Icons.Default.Speed,
                        title = "타이어",
                        subtitle = uiState.tyreStatus?.let { tyre ->
                            val anyIssue = listOf(tyre.leftFront, tyre.rightFront, tyre.leftRear, tyre.rightRear)
                                .any { it.pressureState != 0 || it.airLeakState != 0 }
                            if (anyIssue) "점검 필요" else "정상"
                        } ?: "데이터 로딩 중",
                        statusColor = uiState.tyreStatus?.let { tyre ->
                            val anyIssue = listOf(tyre.leftFront, tyre.rightFront, tyre.leftRear, tyre.rightRear)
                                .any { it.pressureState != 0 || it.airLeakState != 0 }
                            if (anyIssue) WarningOrange else HealthGreen
                        } ?: TextSecondary,
                        onClick = onTyreClick,
                    )
                    SectionCard(
                        icon = Icons.Default.Build,
                        title = "소모품",
                        subtitle = when {
                            uiState.overdueCount > 0 -> "교체 초과 ${uiState.overdueCount}건"
                            uiState.maintenanceItems.any { it.status == MaintenanceStatus.WARNING } -> "교체 임박"
                            else -> "모두 정상"
                        },
                        statusColor = when {
                            uiState.overdueCount > 0 -> CriticalRed
                            uiState.maintenanceItems.any { it.status == MaintenanceStatus.WARNING } -> WarningOrange
                            else -> HealthGreen
                        },
                        onClick = onMaintenanceClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun HealthGauge(score: Int, modifier: Modifier = Modifier) {
    val animatedScore by animateIntAsState(
        targetValue = score,
        animationSpec = tween(durationMillis = 1000, easing = EaseOut),
        label = "healthScore",
    )
    val gaugeColor = when {
        score >= 80 -> HealthGreen
        score >= 50 -> WarningOrange
        else        -> CriticalRed
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier.fillMaxSize().drawBehind {
                val stroke = 18.dp.toPx()
                val inset = stroke / 2f
                val arcSize = Size(size.width - stroke, size.height - stroke)
                val topLeft = Offset(inset / 2f, inset / 2f)

                // 배경 트랙
                drawArc(
                    color = SurfaceVariantDark,
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
                // 점수 호
                drawArc(
                    color = gaugeColor,
                    startAngle = 135f,
                    sweepAngle = 270f * animatedScore / 100f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
            },
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "$animatedScore",
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                color = gaugeColor,
            )
            Text("/ 100", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
    }
}

@Composable
private fun SectionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    statusColor: Color,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = statusColor, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = statusColor)
            }
            Icon(Icons.Default.ChevronRight, null, tint = TextDisabled)
        }
    }
}
