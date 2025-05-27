package mo.voice.memos.di

import mo.voice.memos.data.database.PlatformDatabase
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModules: Module = module {
    single { PlatformDatabase().getDatabase() }
}

fun initKoin() {
    startKoin {
        modules(
            commonModules,
            platformModules
        )
    }
}