package com.bydhealth.monitor.ui.maintenance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bydhealth.monitor.data.local.entity.MaintenanceRecord
import com.bydhealth.monitor.domain.maintenance.MaintenanceItemType
import com.bydhealth.monitor.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MaintenanceHistoryScreen(
    onBack: () -> Unit,
    viewModel: MaintenanceViewModel = hiltViewModel(),
) {
    val history by viewModel.history.collectAsState()
    val dateFormat = remember { SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()) }

    Column(Modifier.fillMaxSize().background(BackgroundDark)) {
        // Top bar
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, null, tint = TextPrimary)
            }
            Text("교체 이력", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        }

        if (history.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("기록된 교체 이력이 없습니다", color = TextSecondary)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(history, key = { it.id }) { record ->
                    HistoryRecordCard(
                        record = record,
                        dateFormat = dateFormat,
                        onDelete = { viewModel.deleteRecord(record) },
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryRecordCard(
    record: MaintenanceRecord,
    dateFormat: SimpleDateFormat,
    onDelete: () -> Unit,
) {
    val typeName = runCatching {
        MaintenanceItemType.valueOf(record.itemType).displayName
    }.getOrDefault(record.itemType)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(typeName, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Spacer(Modifier.height(4.dp))
                Text(
                    "${dateFormat.format(Date(record.replacedAt))}  ·  ${record.mileageAtReplacement}km",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
                if (record.note.isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Text(record.note, style = MaterialTheme.typography.bodyMedium, color = TextDisabled)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, null, tint = TextDisabled)
            }
        }
    }
}
