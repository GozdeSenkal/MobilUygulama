

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}

// Proje düzeyindeki build.gradle dosyası
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Kotlin sürümünü güncelleyin
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
        classpath ("com.android.tools.build:gradle:8.1.2") // Gradle Plugin
        classpath ("com.google.gms:google-services:4.3.15") // Google Services Plugin
      //  classpath ("com.google.firebase:firebase-ml-model-interpreter:24.0.0")
    }
}





