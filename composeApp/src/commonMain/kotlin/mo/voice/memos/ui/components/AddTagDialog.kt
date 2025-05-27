package mo.voice.memos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import mo.voice.memos.data.database.entities.tag.Tag
import mo.voice.memos.ui.theme.Radius
import mo.voice.memos.ui.theme.Spacing
import mo.voice.memos.ui.theme.VSpacing

private val tagColors = listOf(
    Color(0xFFF44336), Color(0xFFE91E63), Color(0xFF9C27B0),
    Color(0xFF673AB7), Color(0xFF3F51B5), Color(0xFF2196F3),
    Color(0xFF03A9F4), Color(0xFF00BCD4), Color(0xFF009688),
    Color(0xFF4CAF50), Color(0xFF8BC34A), Color(0xFFCDDC39),
    Color(0xFFFFEB3B), Color(0xFFFFC107), Color(0xFFFF9800),
    Color(0xFFFF5722), Color(0xFF795548), Color(0xFF9E9E9E),
    Color(0xFF607D8B), Color(0xFF000000)
)

@Composable
fun AddTagDialog(visible: Boolean, onDismiss: () -> Unit, onSave: (Tag) -> Unit) {
    if (!visible) return
    val tagName = remember { mutableStateOf("") }
    val tagColor = remember { mutableStateOf(tagColors.first()) }
    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(
            usePlatformDefaultWidth = true,
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(Radius.M.dp)
            ).padding(Spacing.S.value)
        ) {
            Row(Modifier.fillMaxWidth()) {
                Text(text = "Add Tag", fontSize = MaterialTheme.typography.headlineMedium.fontSize, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(VSpacing.M.value))
            TextField(
                value = tagName.value,
                placeholder = {
                    Text(text = "Tag Name")
                },
                onValueChange = { tagName.value = it })
            ColorPalette(
                onColorSelected = { tagColor.value = it },
                selectedColor = tagColor.value
            )
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors()
                        .copy(
                            contentColor = MaterialTheme.colorScheme.onError,
                            containerColor = MaterialTheme.colorScheme.error
                        )
                ) {
                    Text(text = "Cancel")
                }
                Button(
                    enabled = tagName.value.isNotBlank() && tagName.value.length < 30,
                    onClick = {
                        onSave(Tag(name = tagName.value, color = tagColor.value.toHex()))
                    },
                    colors = ButtonDefaults.buttonColors()
                        .copy(
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                ) {
                    Text(text = "Save")
                }
            }
        }

    }
}

@Composable
fun ColorPalette(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.padding(Spacing.S.value),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        tagColors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color, shape = CircleShape)
                    .clickable { onColorSelected(color) }
                    .then(
                        if (selectedColor == color) Modifier.border(
                            2.dp,
                            MaterialTheme.colorScheme.primary,
                            CircleShape
                        ) else Modifier
                    )
            )
        }
    }
}


fun Color.toHex(): String {
    fun Int.toHexByte(): String = this.coerceIn(0, 255).toString(16).padStart(2, '0').uppercase()

    val r = (red * 255).toInt().toHexByte()
    val g = (green * 255).toInt().toHexByte()
    val b = (blue * 255).toInt().toHexByte()
    val a = (alpha * 255).toInt().toHexByte()

    return "#$a$r$g$b"
}
