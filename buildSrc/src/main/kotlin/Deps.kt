import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.DependencyHandlerScope

@Suppress("SpellCheckingInspection")
object Deps {
    const val kotlinVersion = "1.6.0"
    const val androidGradleTools = "4.1.3"
    const val compileSdkVersion = 33
    const val minSdkVersion = 21
    const val targetSdkVersion = 33
    const val buildToolVersion = "30.0.3"
    const val versionCode = 1
    const val versionName = "1.0"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:1.1.3"
    const val androidMaterial = "com.google.android.material:material:1.2.1"
    const val hiltVersion = "2.38.1"
//    // di
//    implementation "com.google.dagger:hilt-android:2.28-alpha"
//    kapt "com.google.dagger:hilt-android-compiler:2.28-alpha"
//    implementation 'androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha02'
//    kapt 'androidx.hilt:hilt-compiler:1.0.0-alpha02'
}


object Common {
    const val kotlinstbLib = "org.jetbrains.kotlin:kotlin-stdlib:${Deps.kotlinVersion}"
    const val appCompat = "androidx.appcompat:appcompat:1.2.0"
    const val coreKtx = "androidx.core:core-ktx:1.3.2"
    const val junit = "junit:junit:4.13.1"
    const val androidxTestRunner = "androidx.test.ext:junit:1.1.2"
    const val espressoCore = "androidx.test.espresso:espresso-core:3.3.0"
    const val hiltAndroid = "com.google.dagger:hilt-android:2.28-alpha"
    const val hiltLifecycleViewModel = "androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha02"
    const val hiltDaggerCompiler = "com.google.dagger:hilt-android-compiler:2.28-alpha"
    const val hiltAndroidCompiler = "androidx.hilt:hilt-compiler:1.0.0-alpha02"
}

fun DependencyHandlerScope.commonDependencies() {
    "implementation"(Common.kotlinstbLib)
    "implementation"(Common.appCompat)
    "implementation"(Common.coreKtx)
    "testImplementation"(Common.junit)
    "androidTestImplementation"(Common.espressoCore)
    "androidTestImplementation"(Common.androidxTestRunner)
    "implementation"(Common.hiltAndroid)
    "implementation"(Common.hiltLifecycleViewModel)
    "kapt"(Common.hiltDaggerCompiler)
    "kapt"(Common.hiltAndroidCompiler)
}

fun DependencyHandler.mobileAppImplementation(dependencyNotation: Any): Dependency? =
    add("mobileImplementation", dependencyNotation)

fun DependencyHandler.tvAppImplementation(dependencyNotation: Any): Dependency? =
    add("tvImplementation", dependencyNotation)

fun DependencyHandler.fullImplementation(dependencyNotation: Any): Dependency? =
    add("fullImplementation", dependencyNotation)
