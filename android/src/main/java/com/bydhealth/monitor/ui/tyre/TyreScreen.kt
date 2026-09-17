package com.bydhealth.monitor.ui.tyre

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bydhealth.monitor.domain.model.TyreStatus
import com.bydhealth.monitor.ui.theme.*

@Composable
fun TyreScreen(viewModel: TyreViewModel = hiltViewModel()) {
    val tyreStatus by viewModel.tyreStatus.collectAsState()

    Box(Modifier.fillMaxSize().background(BackgroundDark)) {
        if (tyreStatus == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BydBlueBright)
            }
        } else {
            TyreContent(tyreStatus!!)
        }
    }
}

@Composable
private fun TyreContent(status: TyreStatus) {
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        // TPMS 시스템 상태
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            TyreStatusSummaryBadge(status)
        }

        Spacer(Modifier.height(16.dp))

        // 차량 조감도 레이아웃 — 타이어를 2×2로 차량 이미지 주변에 배치
        Box(Modifier.fillMaxWidth().weight(1f)) {
            // 좌전
            TyreDetailCard(
                label = "좌전",
                data = status.leftFront,
                modifier = Modifier.align(Alignment.TopStart).width(130.dp),
            )
            // 우전
            TyreDetailCard(
                label = "우전",
                data = status.rightFront,
                modifier = Modifier.align(Alignment.TopEnd).width(130.dp),
            )
            // 차량 중앙 표시
            VehicleCenterGraphic(modifier = Modifier.align(Alignment.Center))
            // 좌후
            TyreDetailCard(
                label = "좌후",
                data = status.leftRear,
                modifier = Modifier.align(Alignment.BottomStart).width(130.dp),
            )
            // 우후
            TyreDetailCard(
                label = "우후",
                data = status.rightRear,
                modifier = Modifier.align(Alignment.BottomEnd).width(130.dp),
            )
        }

        Spacer(Modifier.height(16.dp))

        // 하단 범례
        TyreLegend()
    }
}

@Composable
private fun VehicleCenterGraphic(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.width(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            color = SurfaceVariantDark,
            modifier = Modifier.fillMaxWidth().height(220.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("BYD", style = MaterialTheme.typography.titleLarge, color = BydBlueBright)
            }
        }
    }
}

@Composable
private fun TyreLegend() {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        LegendItem("정상", HealthGreen)
        LegendItem("과압/저압", WarningOrange)
        LegendItem("누출", CriticalRed)
    }
}

@Composable
private fun LegendItem(label: String, color: androidx.compose.ui.graphics.Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = androidx.compose.foundation.shape.CircleShape,
            color = color,
            modifier = Modifier.size(10.dp),
        ) {}
        Spacer(Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
    }
}
