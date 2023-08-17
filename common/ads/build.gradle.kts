import EncryptionUtils.encrypt
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk=Deps.compileSdkVersion
    buildToolsVersion = Deps.buildToolVersion

    defaultConfig {
        minSdk = Deps.minSdkVersion
        targetSdk = Deps.targetSdkVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFile("consumer-rules.pro")
        val localProperties = Properties().apply {
            try {
                load(rootProject.file("local.properties").inputStream())
            } catch (e: Exception) {
            }
        }
        buildConfigField(
            "String",
            "HOME_BANNER_AD_ID",
            "\"${
                localProperties["HOME_BANNER_AD_ID"]?.toString()
                    ?.encrypt() ?: AdsConfig.HOME_BANNER_AD_ID.encrypt()
            }\""
        )
        buildConfigField(
            "String",
            "HOME_NATIVE_AD_ID",
            "\"${
                localProperties["HOME_NATIVE_AD_ID"]?.toString()
                    ?.encrypt() ?: AdsConfig.HOME_NATIVE_AD_ID.encrypt()
            }\""
        )
        buildConfigField(
            "String",
            "ADD_FILE_INTERSTITIAL_AD_ID",
            "\"${
                localProperties["ADD_FILE_INTERSTITIAL_AD_ID"]?.toString()
                    ?.encrypt() ?: AdsConfig.ADD_FILE_INTERSTITIAL_AD_ID.encrypt()
            }\""
        )
        buildConfigField(
            "String",
            "PLAYER_INTERSTITIAL_AD_ID",
            "\"${
                localProperties["PLAYER_INTERSTITIAL_AD_ID"]?.toString()
                    ?.encrypt() ?: AdsConfig.PLAYER_INTERSTITIAL_AD_ID.encrypt()
            }\""
        )
        buildConfigField(
            "String",
            "ADS_TEST_DEVICE_ID",
            "\"${localProperties["ADS_TEST_DEVICE_ID"]}\""
        )
        resValue(
            "string", "privacy_policy_url",
            "${localProperties["PRIVACY_POLICY_URL"] ?: AdsConfig.PRIVACY_POLICY_URL}"
        )
        resValue(
            "string",
            "admob_publisher_id",
            "${localProperties["ADMOB_PUBLISHER_ID"] ?: AdsConfig.ADMOB_PUBLISHER_ID}"
        )
        resValue(
            "string",
            "admob_app_id",
            "${localProperties["ADMOB_APP_ID"] ?: AdsConfig.ADMOB_APP_ID}"
        )
        buildConfigField(
            "int",
            "BACK_PRESS_CLICKS",
            "${localProperties["BACK_PRESS_CLICKS"] ?: AdsConfig.BACK_PRESS_CLICKS}"
        )
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
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    implementation("com.google.dagger:hilt-android:2.38.1")
    kapt("com.google.dagger:hilt-android-compiler:2.38.1")
    // GDPR
    api("com.github.MFlisar:GDPRDialog:1.4.4")
    // ads
    api("com.github.ixiDev.AdsToolkit:core:0.0.13")
    api("com.github.ixiDev.AdsToolkit:admob:0.0.13")
    api("com.google.android.gms:play-services-ads:21.2.0")

}
