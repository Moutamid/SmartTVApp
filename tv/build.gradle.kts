@file:Suppress("SpellCheckingInspection")

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {

    compileSdk = Deps.compileSdkVersion
    buildToolsVersion = Deps.buildToolVersion

    defaultConfig {
        minSdk = Deps.minSdkVersion
        targetSdk = Deps.targetSdkVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFile("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("this") {
        }
    }
    compileOptions.setSourceCompatibility(JavaVersion.VERSION_1_8)
    compileOptions.setTargetCompatibility(JavaVersion.VERSION_1_8)
    kotlinOptions.jvmTarget = "1.8"
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

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
    // implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    kapt("com.google.dagger:hilt-android-compiler:2.38.1")
    // kapt("androidx.hilt:hilt-compiler:1.0.0-beta01")

    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.4.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.4.1")
    implementation(project(":common"))

    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.leanback:leanback:1.0.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("androidx.activity:activity-ktx:1.4.0")
    implementation("androidx.fragment:fragment-ktx:1.4.1")
//    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.2.0")
//    implementation("androidx.lifecycle:lifecycle-common-java8:2.2.0")

    implementation("com.kirich1409.viewbindingpropertydelegate:viewbindingpropertydelegate:1.4.1")

    implementation("com.intuit.sdp:sdp-android:1.0.6")
}