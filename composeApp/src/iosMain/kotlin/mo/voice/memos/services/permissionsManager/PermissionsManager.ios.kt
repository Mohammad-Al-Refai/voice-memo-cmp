package mo.voice.memos.services.permissionsManager

import androidx.compose.runtime.Composable

actual class PermissionsManager {
    @Composable
    actual fun RequestPermission(
        permission: PermissionType,
        onResult: (PermissionStatus) -> Unit
    ) {
    }

    actual fun getPermissionStatus(
        permission: PermissionType,
        onResult: (PermissionStatus) -> Unit
    ) {
    }
}