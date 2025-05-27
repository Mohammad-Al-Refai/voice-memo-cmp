package mo.voice.memos.di

import mo.voice.memos.data.database.PlatformDatabase
import mo.voice.memos.services.audioRecorderService.AudioRecorderService
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import mo.voice.memos.services.permissionsManager.PermissionsManager
actual val platformModules: Module = module {
    single { PlatformDatabase().getDatabase() }
    single { PermissionsManager() }
    single { AudioRecorderService() }
}

fun initKoin() {
    startKoin {
        modules(
            commonModules,
            platformModules
        )
    }
}