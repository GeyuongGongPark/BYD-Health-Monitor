package com.bydhealth.monitor.ui.pairing

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bydhealth.monitor.ui.theme.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun PairingScreen(viewModel: PairingViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "컴패니언 앱 연결",
            style = MaterialTheme.typography.titleLarge.copy(
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            ),
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "아래 코드를 스마트폰 앱에 입력하면\n알림을 받을 수 있습니다",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondary,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(Modifier.height(40.dp))

        when (state) {
            is PairingState.Loading -> {
                CircularProgressIndicator(color = BydBlueBright)
            }

            is PairingState.Ready -> {
                val pairing = (state as PairingState.Ready).pairing
                PairingCodeCard(
                    code = pairing.code,
                    expiresAt = pairing.expiresAt,
                    onRefresh = viewModel::refresh,
                )
            }

            is PairingState.Error -> {
                val msg = (state as PairingState.Error).message
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "코드 발급 실패",
                        color = CriticalRed,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(text = msg, color = TextSecondary, fontSize = 12.sp)
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = viewModel::refresh,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BydBlueBright),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BydBlueBright),
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("다시 시도")
                    }
                }
            }
        }

        Spacer(Modifier.height(40.dp))

        // 사용 안내
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceVariantDark, RoundedCornerShape(12.dp))
                .padding(16.dp),
        ) {
            Text(
                text = "연결 방법",
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            )
            Spacer(Modifier.height(10.dp))
            listOf(
                "1. 스마트폰에 BYD Health Monitor 앱 설치",
                "2. 앱 실행 후 '차량 연결' 화면 선택",
                "3. 위 6자리 코드 입력",
                "4. 연결 완료 — 이후 알림 자동 수신",
            ).forEach { step ->
                Text(
                    text = step,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(vertical = 3.dp),
                )
            }
        }
    }
}

@Composable
private fun PairingCodeCard(code: String, expiresAt: Long, onRefresh: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault())
    val expiresStr = formatter.format(Instant.ofEpochMilli(expiresAt))

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // 코드 박스
        Box(
            modifier = Modifier
                .background(SurfaceDark, RoundedCornerShape(16.dp))
                .border(1.dp, BydBlueDim, RoundedCornerShape(16.dp))
                .padding(horizontal = 36.dp, vertical = 24.dp),
            contentAlignment = Alignment.Center,
        ) {
            // 6자리 코드를 3자리씩 나눠 표시 (예: 123 456)
            val formatted = "${code.take(3)} ${code.drop(3)}"
            Text(
                text = formatted,
                fontSize = 52.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                color = BydBlueBright,
                letterSpacing = 4.sp,
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${expiresStr}까지 유효",
                color = TextSecondary,
                fontSize = 13.sp,
            )
            Spacer(Modifier.width(12.dp))
            IconButton(onClick = onRefresh, modifier = Modifier.size(28.dp)) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "새 코드 발급",
                    tint = BydBlueBright,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}
