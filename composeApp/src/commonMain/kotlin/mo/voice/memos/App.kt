package mo.voice.memos

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import mo.voice.memos.ui.navigation.RootNav
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        RootNav()
    }
}