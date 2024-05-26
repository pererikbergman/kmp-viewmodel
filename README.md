# Adding ViewModel to a Kotlin Multiplatform Project

In this tutorial, we will go through the steps to integrate ViewModel into a Kotlin Multiplatform (KMP) project. The
goal is to demonstrate how to set up dependencies, create a ViewModel, and use it in your Compose Multiplatform UI. If
you prefer to watch a video tutorial, you can find it here: [YouTube Video Tutorial](https://youtu.be/4ieIpYo1sVg).

If you like this tutorial, it would make me very happy if you also subscribe to my YouTube channel and like my videos.

### Prerequisite

This tutorial builds on top of the previous tutorial about setting up Koin. If you haven't already set up Koin, please
refer to that tutorial first: [Koin Setup Tutorial](https://youtu.be/OqWM0HvjvYM).

## Step 1: Add Dependencies

First, update your `libs.versions.toml` to include necessary dependencies for Koin, ViewModel, and Coroutines.

### `/gradle/libs.versions.toml`

```toml
[versions]
# ...

# Koin
koin = "3.6.0-alpha3"
koinCompose = "3.6.0-alpha3"
koinComposeMultiplatform = "1.2.0-alpha3"

# ViewModel
lifecycleViewModel = "2.8.0"

# Navigation
navigationCompose = "2.7.0-alpha03"

# Coroutines
kotlinxCoroutines = "1.8.0"

[libraries]
# ...

# Koin
koin-android = { module = "io.insert-koin:koin-android", version.ref = "koin" }
koin-androidx-compose = { module = "io.insert-koin:koin-androidx-compose", version.ref = "koinCompose" }
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-compose = { module = "io.insert-koin:koin-compose", version.ref = "koinComposeMultiplatform" }

# ViewModel
lifecycle-viewmodel = { module = "androidx.lifecycle:lifecycle-viewmodel", version.ref = "lifecycleViewModel" }

# Navigation
navigation-compose = { module = "org.jetbrains.androidx.navigation:navigation-compose", version.ref = "navigationCompose" }

# Coroutines
kotlinx-coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "kotlinxCoroutines" }
kotlinx-coroutines-android = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-android", version.ref = "kotlinxCoroutines" }
kotlinx-coroutines-swing = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-swing", version.ref = "kotlinxCoroutines" }

[plugins]
# ...
```

## Step 2: Configure Gradle

Next, update your Gradle build script to include these dependencies.

### `/composeApp/build.gradle.kts`

```kotlin
// ...

kotlin {
    // ...

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            // ...

            // Koin
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)

            // Coroutines
            implementation(libs.kotlinx.coroutines.android)
        }
        commonMain.dependencies {
            // ...

            // Koin
            api(libs.koin.core)
            implementation(libs.koin.compose)

            // ViewModel
            implementation(libs.lifecycle.viewmodel)

            // Navigation
            implementation(libs.navigation.compose)

            // Coroutines
            implementation(libs.kotlinx.coroutines.core)
        }
        desktopMain.dependencies {
            // ...

            // Coroutines
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

// ...
```

## Step 3: Create the ViewModel

Create a `ViewModel` in your common source set.

### `/commonMain/kotlin/MainViewModel.kt`

```kotlin
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val platform: Platform
) : ViewModel() {

    private val _greeting = MutableStateFlow("")
    val greeting = _greeting.asStateFlow()

    init {
        fetchPlatformGreeting()
    }

    private fun fetchPlatformGreeting() {
        viewModelScope.launch {
            _greeting.value = platform.name
        }
    }
}
```

## Step 4: Set Up Dependency Injection

Set up dependency injection for each platform using Koin.

### `/composeApp/src/androidMain/kotlin/di/modules.android.kt`

```kotlin
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
```

### `/composeApp/src/iosMain/kotlin/di/modules.ios.kt`

```kotlin
package di

import IOSPlatform
import Platform
import org.koin.dsl.module
import MainViewModel
import org.koin.core.module.dsl.singleOf

actual val platformModule = module {
    single<Platform> { IOSPlatform() }

    singleOf(::MainViewModel) // Added
}
```

### `/composeApp/src/desktopMain/kotlin/di/modules.jvm.kt`

```kotlin
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
```

## Step 5: Create Koin ViewModel Helper

Create a helper function to get the ViewModel using Koin in Compose.

### `/composeApp/src/commonMain/kotlin/di/koinViewModel.kt`

```kotlin
package di

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.compose.currentKoinScope

@Composable
inline fun <reified T : ViewModel> koinViewModel(): T {
    val scope = currentKoinScope()
    return viewModel {
        scope.get<T>()
    }
}
```

## Step 6: Use the ViewModel in Your Compose App

Finally, use the ViewModel in your Compose UI.

### `/composeApp/src/commonMain/kotlin/App.kt`

```kotlin
@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(appModule, platformModule)
    }
    ) {
        MaterialTheme {
            val viewModel = koinViewModel<MainViewModel>() // Added
            val greeting by viewModel.greeting.collectAsState() // Added

            var showContent by remember { mutableStateOf(false) }
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = { showContent = !showContent }) {
                    Text("Click me!")
                }
                AnimatedVisibility(showContent) {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painterResource(Res.drawable.compose_multiplatform), null)
                        Text("Compose: $greeting") // Updated
                    }
                }
            }
        }
    }
}
```

## Conclusion

In this tutorial, we added a ViewModel to our Kotlin Multiplatform project using Koin for dependency injection and
integrated it into our Compose Multiplatform UI. This setup ensures a scalable architecture with clear separation of
concerns, making your codebase more maintainable and testable.