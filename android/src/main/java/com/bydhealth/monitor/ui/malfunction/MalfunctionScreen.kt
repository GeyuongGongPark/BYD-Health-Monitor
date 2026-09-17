package com.bydhealth.monitor.ui.malfunction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bydhealth.monitor.domain.model.MalfunctionInfo
import com.bydhealth.monitor.domain.model.Severity
import com.bydhealth.monitor.ui.theme.*

@Composable
fun MalfunctionScreen(viewModel: MalfunctionViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        when {
            uiState.isLoading     -> LoadingContent()
            uiState.isAllNormal   -> NormalContent()
            else                  -> MalfunctionList(uiState.activeMalfunctions)
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = BydBlueBright)
    }
}

@Composable
private fun NormalContent() {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(Icons.Default.CheckCircle, null, tint = HealthGreen, modifier = Modifier.size(80.dp))
        Spacer(Modifier.height(16.dp))
        Text("모든 시스템 정상", style = MaterialTheme.typography.headlineMedium, color = HealthGreen)
        Spacer(Modifier.height(8.dp))
        Text("감지된 고장 코드가 없습니다", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}

@Composable
private fun MalfunctionList(malfunctions: List<MalfunctionInfo>) {
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.Warning, null, tint = WarningOrange, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(8.dp))
            Text("고장 감지 ${malfunctions.size}건", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        }
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(malfunctions, key = { it.code }) { MalfunctionCard(it) }
        }
    }
}

@Composable
private fun MalfunctionCard(info: MalfunctionInfo) {
    val (bgColor, iconColor, icon) = info.severity.style()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        info.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    SeverityBadge(info.severity, iconColor)
                }
                if (info.action.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Text("→ ${info.action}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun SeverityBadge(severity: Severity, color: Color) {
    val label = when (severity) {
        Severity.CRITICAL -> "긴급"
        Severity.WARNING  -> "주의"
        Severity.INFO     -> "정보"
    }
    Surface(shape = RoundedCornerShape(4.dp), color = color.copy(alpha = 0.2f)) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.Bold,
        )
    }
}

private data class SeverityStyle(val bg: Color, val icon: Color, val vector: ImageVector)

private fun Severity.style() = when (this) {
    Severity.CRITICAL -> Triple(CriticalRedContainer,      CriticalRed,    Icons.Default.Error)
    Severity.WARNING  -> Triple(WarningOrangeContainer,    WarningOrange,  Icons.Default.Warning)
    Severity.INFO     -> Triple(InfoYellowContainer,       InfoYellow,     Icons.Default.Info)
}
