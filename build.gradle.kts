buildscript {

    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://plugins.gradle.org/m2/") }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Deps.kotlinVersion}")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.38.1")
        //classpath("android.arch.navigation:navigation-safe-args-gradle-plugin:2.3.1")
        classpath("com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin:8.9.4")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            setUrl("https://oss.jfrog.org/artifactory/oss-snapshot-local/")
        }
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://plugins.gradle.org/m2/") }

        // Yodo1
        maven { setUrl ("https://artifact.bytedance.com/repository/pangle") }
        maven { setUrl ("https://android-sdk.is.com") }
        maven { setUrl ("https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea") }

    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

/*
tasks.register("generateKeyStore", Exec::class) {
    workingDir("./app")
    executable("sh")
    args("-c", "echo ${System.getenv("CI_KEYSTORE")} | base64 --decode > ci_key.jks")
}*/