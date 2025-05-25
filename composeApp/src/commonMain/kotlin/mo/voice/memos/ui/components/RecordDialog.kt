package mo.voice.memos.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Save
import mo.voice.memos.ui.theme.IconSize
import mo.voice.memos.ui.theme.Spacing


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onStopRecoding: () -> Unit,
    amplitude: Int
) {
    if (!visible) return
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(visible) {
        if (visible) sheetState.show() else sheetState.hide()
    }
    if (visible) {
        ModalBottomSheet(
            contentWindowInsets = { BottomSheetDefaults.windowInsets.add(WindowInsets.navigationBars) },
            containerColor = MaterialTheme.colorScheme.surface,
            sheetState = sheetState,
            onDismissRequest = onDismiss
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Record a memo",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    textAlign = TextAlign.Center
                )
                Column(
                    modifier = Modifier.padding(Spacing.L.value).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AnimatedAmplitudeCircle(amplitude)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(onClick = onStopRecoding) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.Save,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(IconSize.M.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedAmplitudeCircle(amplitude: Int) {
    // Normalize amplitude 0..1
    val normalized = (amplitude / 32767f).coerceIn(0f, 1f)

    // Sensitivity factor - increase this to make circle more reactive
    val sensitivity = 3f

    // Apply sensitivity and clamp max to 1f
    val adjusted = (normalized * sensitivity).coerceIn(0f, 1f)

    val animatedRadius by animateFloatAsState(
        targetValue = adjusted,
        animationSpec = tween(durationMillis = 100, easing = LinearEasing)
    )

    val maxRadius = 120f // bigger max radius in dp

    Box(
        modifier = Modifier
            .size((maxRadius * 2).dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radiusPx = animatedRadius * maxRadius * density
            drawCircle(
                color = Color.Red,
                radius = radiusPx.coerceAtLeast(20f * density), // min radius bigger
                center = center
            )
        }
    }
}
