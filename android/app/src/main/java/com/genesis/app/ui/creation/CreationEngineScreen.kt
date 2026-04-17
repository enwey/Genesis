package com.genesis.app.ui.creation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.genesis.app.data.model.CreationResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreationEngineScreen(
    viewModel: CreationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var touchPosition by remember { mutableStateOf<Offset?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    touchPosition = offset
                }
            }
    ) {
        // 1. 環境視覺：無限延伸的淺灰色網格
        InfiniteGridBackground(touchPosition)

        // 2. 言靈法師：半透明發光小人
        SpiritAvatar(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 64.dp, bottom = 120.dp),
            isCasting = uiState.isLoading
        )

        // 3. 核心造物區（VFX）
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CreationVFXManager(uiState)
        }

        // 4. 介面設計：極簡未來感毛玻璃面板
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            CreationInputPanel(
                prompt = uiState.prompt,
                onPromptChange = { viewModel.onEvent(CreationUiEvent.OnPromptChanged(it)) },
                onGenerate = { viewModel.onEvent(CreationUiEvent.OnGenerateClicked) },
                isLoading = uiState.isLoading,
                statusMessage = uiState.statusMessage
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun InfiniteGridBackground(touchOffset: Offset?) {
    val transition = rememberInfiniteTransition()
    val pulseAlpha by transition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(3000), RepeatMode.Reverse)
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val gridSize = 60.dp.toPx()
        val gridColor = Color.LightGray.copy(alpha = pulseAlpha)
        
        // 繪製橫線
        for (y in 0..size.height.toInt() step gridSize.toInt()) {
            drawLine(gridColor, Offset(0f, y.toFloat()), Offset(size.width, y.toFloat()), strokeWidth = 1f)
        }
        // 繪製縱線
        for (x in 0..size.width.toInt() step gridSize.toInt()) {
            drawLine(gridColor, Offset(x.toFloat(), 0f), Offset(x.toFloat(), size.height), strokeWidth = 1f)
        }

        // 觸摸處發出淡藍色光波
        touchOffset?.let {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF00B4FF).copy(alpha = 0.3f), Color.Transparent),
                    center = it,
                    radius = 300f
                ),
                radius = 300f,
                center = it
            )
        }
    }
}

@Composable
fun SpiritAvatar(modifier: Modifier = Modifier, isCasting: Boolean) {
    val infiniteTransition = rememberInfiniteTransition()
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearOutSlowInEasing), RepeatMode.Reverse)
    )
    
    val runeRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Restart)
    )

    Box(modifier = modifier.offset(y = floatY.dp)) {
        Canvas(modifier = Modifier.size(100.dp, 160.dp)) {
            val glowColor = Color(0xFF00B4FF).copy(alpha = 0.6f)
            drawCircle(glowColor, radius = 15.dp.toPx(), center = Offset(50.dp.toPx(), 20.dp.toPx()))
            drawLine(glowColor, Offset(50.dp.toPx(), 35.dp.toPx()), Offset(50.dp.toPx(), 80.dp.toPx()), strokeWidth = 3f)
            drawLine(glowColor, Offset(50.dp.toPx(), 50.dp.toPx()), Offset(20.dp.toPx(), 70.dp.toPx()), strokeWidth = 2f)
            drawLine(glowColor, Offset(50.dp.toPx(), 50.dp.toPx()), Offset(80.dp.toPx(), 70.dp.toPx()), strokeWidth = 2f)
            drawLine(glowColor, Offset(50.dp.toPx(), 80.dp.toPx()), Offset(30.dp.toPx(), 130.dp.toPx()), strokeWidth = 2f)
            drawLine(glowColor, Offset(50.dp.toPx(), 80.dp.toPx()), Offset(70.dp.toPx(), 130.dp.toPx()), strokeWidth = 2f)
        }

        if (isCasting) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-40).dp)
                    .size(40.dp)
                    .rotate(runeRotation)
                    .background(Color(0xFF00B4FF).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFF00B4FF), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
fun CreationInputPanel(
    prompt: String,
    onPromptChange: (String) -> Unit,
    onGenerate: () -> Unit,
    isLoading: Boolean,
    statusMessage: String?
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
        color = Color.White.copy(alpha = 0.7f),
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            if (isLoading) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Hub, contentDescription = null, tint = Color(0xFF00B4FF), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = statusMessage ?: "正在連接神經網路...",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF00B4FF),
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = prompt,
                onValueChange = onPromptChange,
                placeholder = { Text("輸入言靈...", fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onGenerate,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1B1F)),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading && prompt.isNotBlank()
            ) {
                Text(if (isLoading) "編織中..." else "發動創世", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CreationVFXManager(uiState: CreationUiState) {
    val infiniteTransition = rememberInfiniteTransition()
    val scanY by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse)
    )

    if (uiState.isLoading) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(200.dp)) {
                drawCircle(
                    brush = Brush.radialGradient(listOf(Color(0xFF00B4FF).copy(alpha = 0.4f), Color.Transparent)),
                    radius = size.width / 2
                )
                drawCircle(Color(0xFF00B4FF), radius = size.width / 2, style = Stroke(2f))
            }
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(120.dp).blur(if (uiState.progress < 0.5f) 8.dp else 2.dp),
                tint = Color(0xFF00B4FF).copy(alpha = 0.6f)
            )
            HorizontalDivider(
                modifier = Modifier.width(150.dp).offset(y = scanY.dp),
                color = Color(0xFF00B4FF),
                thickness = 2.dp
            )
        }
    } else if (uiState.result != null) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ModelViewerPlaceholder(url = uiState.result.modelUrl)
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                color = Color.Black.copy(alpha = 0.8f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "ID: ${uiState.result.assetId.takeLast(6)}",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun ModelViewerPlaceholder(@Suppress("UNUSED_PARAMETER") url: String) {
    Box(
        modifier = Modifier.size(200.dp).background(Color.FfmSurface, CircleShape).border(1.dp, Color.LightGray, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text("3D OBJECT", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

private val Color.Companion.FfmSurface get() = Color(0xFFF8F8F8)
