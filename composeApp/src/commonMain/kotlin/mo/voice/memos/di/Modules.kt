package mo.voice.memos.di

import mo.voice.memos.ui.screens.landing.LandingViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

expect val platformModules: Module
val commonModules = module {

    viewModelOf(::LandingViewModel)
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(platformModules, commonModules)
    }
}