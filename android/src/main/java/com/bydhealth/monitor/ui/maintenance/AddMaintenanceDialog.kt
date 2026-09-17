package com.bydhealth.monitor.ui.maintenance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bydhealth.monitor.domain.maintenance.MaintenanceItemType
import com.bydhealth.monitor.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddMaintenanceDialog(
    type: MaintenanceItemType,
    currentMileage: Int,
    onConfirm: (mileage: Int, dateMs: Long, note: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var mileageText by remember { mutableStateOf(currentMileage.toString()) }
    var noteText by remember { mutableStateOf("") }
    val dateMs = remember { System.currentTimeMillis() }
    val dateLabel = remember {
        SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(Date(dateMs))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text("${type.displayName} 교체 기록", color = TextPrimary)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("교체 일자: $dateLabel", color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium)

                OutlinedTextField(
                    value = mileageText,
                    onValueChange = { mileageText = it.filter { c -> c.isDigit() } },
                    label = { Text("교체 시 주행거리 (km)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BydBlueBright,
                        focusedLabelColor = BydBlueBright,
                        unfocusedBorderColor = DividerDark,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                    ),
                )
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("메모 (선택)") },
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BydBlueBright,
                        focusedLabelColor = BydBlueBright,
                        unfocusedBorderColor = DividerDark,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                    ),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val mileage = mileageText.toIntOrNull() ?: return@TextButton
                    onConfirm(mileage, dateMs, noteText)
                },
            ) { Text("저장", color = BydBlueBright) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소", color = TextSecondary) }
        },
    )
}
