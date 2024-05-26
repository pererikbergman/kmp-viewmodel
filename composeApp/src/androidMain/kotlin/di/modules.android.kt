package di

import AndroidPlatform
import Platform
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import MainViewModel

actual val platformModule = module {
    single<Platform> { AndroidPlatform() }

    viewModelOf(::MainViewModel)
}