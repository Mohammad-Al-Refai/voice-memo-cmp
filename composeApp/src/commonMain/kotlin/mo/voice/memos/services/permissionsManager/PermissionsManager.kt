package mo.voice.memos.services.permissionsManager

import androidx.compose.runtime.Composable

enum class PermissionType { AUDIO_RECORD }
expect class PermissionsManager {
    @Composable
    fun RequestPermission(permission: PermissionType, onResult: (PermissionStatus) -> Unit)
    fun getPermissionStatus(permission: PermissionType, onResult: (PermissionStatus) -> Unit)
}

enum class PermissionStatus { GRANTED, DENIED, NOT_DETERMINED }