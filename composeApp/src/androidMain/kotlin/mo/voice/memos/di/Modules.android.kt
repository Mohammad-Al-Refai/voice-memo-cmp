package mo.voice.memos.di

import mo.voice.memos.services.audioRecorderService.AudioRecorderService
import mo.voice.memos.services.permissionsManager.PermissionsManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModules: Module = module {
    single { PermissionsManager(androidContext()) }
    single { AudioRecorderService(androidContext()) }
}