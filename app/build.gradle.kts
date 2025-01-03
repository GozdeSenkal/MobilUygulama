plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.yeniproje"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.yeniproje"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        compose = true // Jetpack Compose'u etkinleştir
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3" // Compose sürümüne uygun olarak ayarlandı
    }
}

dependencies {
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation ("androidx.room:room-runtime:2.5.1")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Room
    val room_version = "2.6.1"
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    //Viewmodel + livedata + lifecycle

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    kapt(libs.androidx.lifecycle.compiler)
    implementation(libs.kotlinx.coroutines.android)

    // Compose
    implementation(libs.androidx.core.ktx)
    implementation("androidx.compose.ui:ui:1.5.3")
    implementation("androidx.compose.material:material:1.5.3")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.3")
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.3")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation ("androidx.compose.foundation:foundation:1.4.0")
    implementation ("androidx.navigation:navigation-compose:2.5.3")
    implementation ("androidx.compose.material3:material3:1.0.0")

    // UI and Utility Libraries
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("androidx.compose.material3:material3:1.2.0-alpha02")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.navigation:navigation-compose:2.7.1")
    // Material3 güncellemesi
    implementation("androidx.compose.material3:material3:1.3.0")
    // Compose ve diğer bağımlılıklar
    implementation("androidx.compose.foundation:foundation:1.5.3")
    implementation ("androidx.compose.material3:material3:1.1.0")
    implementation ("androidx.compose.ui:ui:1.5.0")
    implementation ("androidx.compose.material:material:1.4.0")
    implementation("androidx.activity:activity-compose:1.8.0")

    implementation ("com.google.firebase:firebase-auth:22.1.1")
    implementation ("com.google.firebase:firebase-database:20.3.0")
    implementation ("com.google.android.gms:play-services-basement:18.3.0")
    implementation ("com.github.bumptech.glide:glide:4.13.0")
    kapt ("com.github.bumptech.glide:compiler:4.13.0")
    implementation ("com.github.bumptech.glide:glide:4.11.0")
    //implementation ("com.google.firebase:firebase-ml-model-interpreter:24.0.0")
    //implementation ("com.google.firebase:firebase-ml-model-interpreter:24.0.3")
    implementation ("com.google.firebase:firebase-ml-vision:24.0.3")
    implementation ("com.google.firebase:firebase-ml-vision-image-label-model:20.0.0")
    implementation ("com.google.firebase:firebase-storage:20.1.0")
    //implementation ("com.google.firebase:firebase-appcheck-safetynet:17.0.0")





}
