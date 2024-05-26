package di

import Greeter
import Greeting
import org.koin.core.module.Module
import org.koin.dsl.module

val appModule = module {
    factory<Greeter> { Greeting(get()) }
}

expect val platformModule: Module