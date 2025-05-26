package mo.voice.memos.ui.screens.landing

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.AngleRight
import compose.icons.fontawesomeicons.solid.CaretRight
import mo.voice.memos.ui.components.AddTagDialog
import mo.voice.memos.ui.theme.HSpacing
import mo.voice.memos.ui.theme.IconSize
import mo.voice.memos.ui.theme.Radius
import mo.voice.memos.ui.theme.Spacing
import mo.voice.memos.ui.theme.VSpacing
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(vm: LandingViewModel = koinViewModel(), onTagClick: (Int) -> Unit) {
    val state by vm.container.stateFlow.collectAsStateWithLifecycle()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Voice Memos") }
            )
        },
        floatingActionButton = {
            Button(onClick = vm::onAddTagClick) {
                Text("Add Tag")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        AddTagDialog(
            visible = state.isAddTagDialogVisible,
            onDismiss = vm::onDismissAddTagDialog,
            onSave = vm::onSaveTag
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding(),
                start = HSpacing.M.value,
                end = HSpacing.M.value
            )
        ) {
            Text(
                text = "All Tags",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize
            )
            Spacer(Modifier.height(VSpacing.S.value))
            if (state.isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
                return@Scaffold
            }
            if (state.tags.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No Tags Added Yet.")
                }
                return@Scaffold
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = VSpacing.XL2.value)
            ) {
                items(state.tags.size) {
                    TagItemViewer(tag = state.tags[it], onTagClick = {
                        onTagClick(state.tags[it].id)
                    })
                    Spacer(Modifier.height(VSpacing.S.value))
                }
            }
        }
    }
}

@Composable
private fun TagItemViewer(tag: TagItem, onTagClick: () -> Unit) {
    val backgroundColor = tag.color.copy(alpha = 0.3f)
    val contentColor = if (backgroundColor.luminance() < 0.5f) Color.Black else Color.White
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTagClick() }
            .background(backgroundColor, shape = RoundedCornerShape(Radius.M.dp))
            .padding(Spacing.S.value),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = tag.name,
                color = contentColor,
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                fontWeight = FontWeight.SemiBold
            )
            Text(text = "${tag.voiceNotesCount} voice notes", color = contentColor)
        }

        Icon(
            imageVector = FontAwesomeIcons.Solid.AngleRight,
            contentDescription = null,
            modifier = Modifier.size(IconSize.S.dp),
            tint = contentColor
        )
    }

}