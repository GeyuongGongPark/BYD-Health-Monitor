package com.bydhealth.monitor.ui.maintenance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bydhealth.monitor.domain.maintenance.MaintenanceItemStatus
import com.bydhealth.monitor.domain.maintenance.MaintenanceItemType
import com.bydhealth.monitor.domain.maintenance.MaintenanceStatus
import com.bydhealth.monitor.ui.theme.*

@Composable
fun MaintenanceScreen(
    onHistoryClick: () -> Unit,
    viewModel: MaintenanceViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var dialogType by remember { mutableStateOf<MaintenanceItemType?>(null) }

    Box(Modifier.fillMaxSize().background(BackgroundDark)) {
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BydBlueBright)
            }
        } else {
            Column(Modifier.fillMaxSize()) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("소모품 관리", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                    TextButton(onClick = onHistoryClick) {
                        Text("교체 이력", color = BydBlueBright)
                    }
                }

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(uiState.items, key = { it.type.name }) { item ->
                        MaintenanceItemCard(
                            item = item,
                            onAddClick = { dialogType = item.type },
                        )
                    }
                }
            }
        }
    }

    dialogType?.let { type ->
        AddMaintenanceDialog(
            type = type,
            currentMileage = uiState.currentMileage,
            onConfirm = { mileage, dateMs, note ->
                viewModel.addRecord(type, mileage, dateMs, note)
                dialogType = null
            },
            onDismiss = { dialogType = null },
        )
    }
}

@Composable
private fun MaintenanceItemCard(
    item: MaintenanceItemStatus,
    onAddClick: () -> Unit,
) {
    val (statusColor, statusLabel) = item.status.style()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(item.type.displayName, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Spacer(Modifier.width(8.dp))
                    StatusChip(statusLabel, statusColor)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    buildString {
                        if (item.lastReplacedAt > 0L) {
                            append("교체 주기 ${item.type.intervalKm / 1000}만km")
                            if (item.remainingKm > 0) append(" · 남은 거리 ${item.remainingKm}km")
                            else append(" · 교체 ${-item.remainingKm}km 초과")
                        } else {
                            append("교체 기록 없음")
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }
            IconButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "교체 기록 추가", tint = BydBlueBright)
            }
        }
    }
}

@Composable
private fun StatusChip(label: String, color: Color) {
    Surface(shape = RoundedCornerShape(4.dp), color = color.copy(alpha = 0.2f)) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelMedium,
            color = color,
        )
    }
}

private fun MaintenanceStatus.style() = when (this) {
    MaintenanceStatus.OVERDUE  -> Pair(CriticalRed,    "교체 초과")
    MaintenanceStatus.WARNING  -> Pair(WarningOrange,  "교체 임박")
    MaintenanceStatus.NORMAL   -> Pair(HealthGreen,    "정상")
}
