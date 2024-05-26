package di

import IOSPlatform
import Platform
import org.koin.dsl.module
import MainViewModel
import org.koin.core.module.dsl.singleOf

actual val platformModule = module {
    single<Platform> { IOSPlatform() }

    singleOf(::MainViewModel)
}