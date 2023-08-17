@file:Suppress("RemoveSingleExpressionStringTemplate")

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.mikepenz.aboutlibraries.plugin")
}

android {
    compileSdk = Deps.compileSdkVersion
    buildToolsVersion = Deps.buildToolVersion

    defaultConfig {
        applicationId = AppConfig.APP_PACKAGE_NAME
        minSdk = Deps.minSdkVersion
        targetSdk = Deps.targetSdkVersion
        versionCode = 713770 //AppConfig.APP_VERSION_CODE
        versionName = "0.0.5.2" //AppConfig.APP_VERSION_NAME
        testInstrumentationRunner = "com.ixidev.smarttvaapp.AppTestsRunner"
        multiDexEnabled = true
        resourceConfigurations += setOf("en")

    }
    signingConfigs {
        this.create("debugkey") {
            storeFile = file(project.gradle.gradleUserHomeDir.parent + "/.android/debug.keystore")
            keyAlias = "androiddebugkey"
            keyPassword = "android"
            storePassword = "android"
        }
        if (file("ci_key.jks").exists()) {
            create("cikey") {
                storeFile = file("ci_key.jks")
                keyAlias = System.getenv("CI_KEYSTORE_ALIAS")
                keyPassword = System.getenv("CI_KEYSTORE_KEY_PASSWORD")
                storePassword = System.getenv("CI_KEYSTORE_STORE_PASSWORD")
            }
        }
    }
    buildTypes {
        getByName("release") {
            if (signingConfigs.asMap["cikey"] != null)
                signingConfig = signingConfigs.getByName("cikey")
            isMinifyEnabled = false // Yodo1 : old was true
            isShrinkResources = false // Yodo1 : old was true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        flavorDimensions.add("versions")
        productFlavors {
            create("tv") {
                dimension = "versions"
                multiDexEnabled = true
                versionNameSuffix = "-tv"
            }
            create("mobile") {
                dimension = "versions"
                multiDexEnabled = true
                versionNameSuffix = "-mobile"
            }
            create("full") {
                dimension = "versions"
                multiDexEnabled = true
            }
        }
    }
    compileOptions.setSourceCompatibility(JavaVersion.VERSION_1_8)
    compileOptions.setTargetCompatibility(JavaVersion.VERSION_1_8)
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    implementation("com.fxn769:stash:1.3.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Deps.kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-common:${Deps.kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Deps.kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Deps.kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${Deps.kotlinVersion}")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.core:core-ktx:1.7.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    implementation("com.google.dagger:hilt-android:2.38.1")
    //implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    kapt("com.google.dagger:hilt-android-compiler:2.38.1")
    // kapt("androidx.hilt:hilt-compiler:1.0.0-beta01")

    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.fragment:fragment-ktx:1.4.1")
    implementation(project(":common"))

    // mobile only
    mobileAppImplementation(project(path = ":mobile"))
    fullImplementation(project(path = ":mobile"))
    // tv
    tvAppImplementation(project(path = ":tv"))
    fullImplementation(project(path = ":tv"))
    // Unit test
    androidTestImplementation("androidx.test:runner:1.4.0")
    // testImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("androidx.test:core-ktx:1.4.0")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.3")
    // testImplementation("androidx.test.ext:junit-ktx:1.1.2")
    androidTestImplementation("androidx.test:rules:1.4.0")
    testImplementation("com.google.dagger:hilt-android-testing:2.38.1")
    kaptTest("com.google.dagger:hilt-android-compiler:2.38.1")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.38.1")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.38.1")
    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")
    //testImplementation("androidx.arch.core:core-testing:2.1.0")

}
