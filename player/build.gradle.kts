plugins {
    id("com.android.library")
    id("kotlin-android")
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
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("this") {
        }
    }
    buildFeatures {
        viewBinding = true
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
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
    api("com.google.android.exoplayer:exoplayer:2.16.0")
    api ("com.google.android.exoplayer:extension-cast:2.16.0")
    api ("com.google.android.gms:play-services-cast-framework:21.2.0")
    implementation("com.kirich1409.viewbindingpropertydelegate:viewbindingpropertydelegate:1.4.1")
    implementation ("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")

    // Material Design Components
    implementation("com.android.support:design:28.0.0")
    implementation("com.android.support:appcompat-v7:28.0.0")

    // Anko
    implementation("org.jetbrains.anko:anko:0.10.4")
    implementation("androidx.activity:activity-ktx:1.4.0")


}