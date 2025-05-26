package mo.voice.memos.ui.screens.tagManager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Microphone
import mo.voice.memos.services.permissionsManager.PermissionType
import mo.voice.memos.ui.components.AudioPlayerItem
import mo.voice.memos.ui.components.RecordDialog
import mo.voice.memos.ui.theme.HSpacing
import mo.voice.memos.ui.theme.IconSize
import mo.voice.memos.ui.theme.VSpacing
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagManagerScreen(
    tagId: Int,
    vm: TagManagerViewModel = koinViewModel(parameters = { parametersOf(tagId) })
) {
    val state by vm.container.stateFlow.collectAsStateWithLifecycle()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("${state.tagName} voice notes") }
            )
        },
        floatingActionButton = {
            IconButton(onClick = vm::onRecordButtonClick) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Microphone,
                    contentDescription = null,
                    modifier = Modifier.size(IconSize.M.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        if (state.shouldRequestRecordPermission) {
            vm.permissionsManager.RequestPermission(
                permission = PermissionType.AUDIO_RECORD,
                onResult = vm::onRecordPermissionResult
            )
        }
        RecordDialog(
            visible = state.isRecordDialogOpen,
            onDismiss = vm::onDismissRecordDialog,
            onStopRecoding = vm::onStopRecording,
            amplitude = state.audioAmplitude
        )
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
        if (state.isError) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Something went wrong.", color = MaterialTheme.colorScheme.error)
            }
            return@Scaffold
        }
        Column(
            modifier = Modifier.fillMaxSize().padding(
                vertical = innerPadding.calculateTopPadding(),
                horizontal = HSpacing.M.value
            )
        ) {
            if (state.records.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No Records Added Yet.")
                }
                return@Scaffold
            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.records.size) {
                    AudioPlayerItem(recordFile = state.records[it])
                    Spacer(Modifier.height(VSpacing.S.value))
                }
            }
        }
    }
}

