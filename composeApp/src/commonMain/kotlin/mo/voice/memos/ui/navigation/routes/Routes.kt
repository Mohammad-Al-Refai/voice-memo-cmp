package mo.voice.memos.ui.navigation.routes

import kotlinx.serialization.Serializable

@Serializable
sealed class AppRoutes {

    @Serializable
    data object Landing : AppRoutes()

    @Serializable
    data class TagManager(val tagId: Int) : AppRoutes()
}