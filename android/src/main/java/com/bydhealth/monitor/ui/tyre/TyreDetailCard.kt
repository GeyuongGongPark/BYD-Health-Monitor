package com.bydhealth.monitor.ui.tyre

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bydhealth.monitor.domain.model.TyreData
import com.bydhealth.monitor.domain.model.TyreStatus
import android.hardware.bydauto.tyre.BYDAutoTyreDevice.*
import com.bydhealth.monitor.ui.theme.*

@Composable
fun TyreDetailCard(
    label: String,
    data: TyreData,
    modifier: Modifier = Modifier,
) {
    val pressureColor = when (data.pressureState) {
        TYRE_PRESSURE_STATE_OVERPRESSURE  -> WarningOrange
        TYRE_PRESSURE_STATE_UNDERPRESSURE -> CriticalRed
        else                              -> HealthGreen
    }
    val hasLeak = data.airLeakState != TYRE_AIR_LEAK_STATE_NORMAL
    val isHighTemp = data.temperatureState in listOf(
        TYRE_TEMPERATURE_STATE_HIGH, TYRE_TEMPERATURE_STATE_SUPER_HIGH
    )

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(Modifier.height(6.dp))

            // 압력 수치
            Text(
                text = if (data.pressureValue > 0) "${data.pressureValue} kPa" else "--",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = pressureColor,
            )

            Spacer(Modifier.height(6.dp))

            // 상태 배지들
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (hasLeak) StatusBadge("누출", CriticalRed)
                if (isHighTemp) StatusBadge("고온", WarningOrange)
                if (!hasLeak && !isHighTemp) StatusBadge("정상", HealthGreen)
            }
        }
    }
}

@Composable
private fun StatusBadge(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.2f),
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelMedium,
            color = color,
        )
    }
}

@Composable
fun TyreStatusSummaryBadge(status: TyreStatus) {
    val systemColor = when (status.systemState) {
        TYRE_SYSTEM_STATE_NORMAL       -> HealthGreen
        TYRE_SYSTEM_STATE_SELF_CHECKING -> BydBlueBright
        else                           -> CriticalRed
    }
    val systemLabel = when (status.systemState) {
        TYRE_SYSTEM_STATE_NORMAL        -> "TPMS 정상"
        TYRE_SYSTEM_STATE_SELF_CHECKING -> "TPMS 점검 중"
        TYRE_SYSTEM_STATE_SIGNAL_ANOMAL -> "TPMS 신호 이상"
        TYRE_SYSTEM_STATE_BREAKDOWN     -> "TPMS 고장"
        else                            -> "TPMS 상태 알 수 없음"
    }
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = systemColor.copy(alpha = 0.15f),
    ) {
        Text(
            systemLabel,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelLarge,
            color = systemColor,
        )
    }
}
