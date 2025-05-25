package mo.voice.memos.services.permissionsManager

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat

actual class PermissionsManager(private val context: Context) {
    @Composable
    actual fun RequestPermission(
        permission: PermissionType,
        onResult: (PermissionStatus) -> Unit
    ) {
        val androidPermission: String? = when (permission) {
            PermissionType.AUDIO_RECORD -> android.Manifest.permission.RECORD_AUDIO
            else -> null
        }

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                onResult(if (isGranted) PermissionStatus.GRANTED else PermissionStatus.DENIED)
            }
        )

        LaunchedEffect(permission) {
            if (androidPermission == null) return@LaunchedEffect
            when (isPermissionGranted(context, androidPermission)) {
                PermissionStatus.GRANTED -> onResult(PermissionStatus.GRANTED)
                PermissionStatus.DENIED -> permissionLauncher.launch(androidPermission)
                PermissionStatus.NOT_DETERMINED -> {
                    /*do nothing*/
                }
            }
        }
    }

    actual fun getPermissionStatus(
        permission: PermissionType,
        onResult: (PermissionStatus) -> Unit
    ) {
        when (permission) {
            PermissionType.AUDIO_RECORD -> onResult(
                isPermissionGranted(
                    context,
                    android.Manifest.permission.RECORD_AUDIO
                )
            )
        }
    }

    private fun isPermissionGranted(context: Context, permission: String): PermissionStatus =
        if (ContextCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED) {
            PermissionStatus.GRANTED
        } else {
            PermissionStatus.DENIED
        }

}