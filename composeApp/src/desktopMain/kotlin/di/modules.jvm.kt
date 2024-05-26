package di

import JVMPlatform
import Platform
import org.koin.dsl.module
import MainViewModel
import org.koin.core.module.dsl.singleOf

actual val platformModule = module {
    single<Platform> { JVMPlatform() }

    singleOf(::MainViewModel)
}