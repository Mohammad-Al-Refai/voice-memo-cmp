package mo.voice.memos.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import mo.voice.memos.ui.navigation.routes.AppRoutes
import mo.voice.memos.ui.screens.landing.LandingScreen
import mo.voice.memos.ui.screens.tagManager.TagManagerScreen

@Composable
fun RootNav(navigator: NavHostController = rememberNavController()) {
    NavHost(
        startDestination = AppRoutes.Landing, navController = navigator,
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)
    ) {
        composable<AppRoutes.Landing> {
            LandingScreen(onTagClick = {
                navigator.navigate(AppRoutes.TagManager(it))
            })
        }
        composable<AppRoutes.TagManager> {
            val tagId = it.toRoute<AppRoutes.TagManager>().tagId
            TagManagerScreen(tagId)
        }
    }
}