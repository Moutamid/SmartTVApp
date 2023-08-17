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
        resValue("string", "app_name", AppConfig.APP_NAME)
        resValue("string", "app_package_name", AppConfig.APP_PACKAGE_NAME)
        resValue("string", "dev_email", AdsConfig.DEV_EMAIL)
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
    api("androidx.activity:activity-ktx:1.4.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    implementation("com.google.dagger:hilt-android:2.38.1")
    //implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    kapt("com.google.dagger:hilt-android-compiler:2.38.1")
    //kapt("androidx.hilt:hilt-compiler:1.0.0-beta01")
    // Room ktx
    api("androidx.room:room-ktx:2.4.2")
    kapt("androidx.room:room-compiler:2.4.2")
    api("androidx.paging:paging-runtime-ktx:3.1.1")
    api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
    api("io.coil-kt:coil:1.3.2")
//    api("org.permissionsdispatcher:permissionsdispatcher:4.7.0")
//    kapt("org.permissionsdispatcher:permissionsdispatcher-processor:4.7.0")
    api("com.github.TutorialsAndroid:FilePicker:v8.0.19")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Deps.kotlinVersion}")
    api(project(":common:ads"))
    api("com.google.android.play:review-ktx:2.0.1")

    implementation("com.google.android.material:material:1.5.0")
    api("dev.dworks.libs:volleyplus:0.1.4")
    api("androidx.room:room-paging:2.4.2")
    implementation("androidx.work:work-runtime-ktx:2.7.1")
}