plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.roots"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.roots"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.navigation.compose)
    implementation ("androidx.security:security-crypto:1.1.0-alpha06")


    implementation(libs.play.services.maps)
    implementation(libs.androidx.media3.common.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("io.coil-kt:coil-gif:2.4.0")
    implementation("androidx.compose.animation:animation:1.5.1")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.compose.foundation:foundation:1.6.1")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.maps.android:maps-compose:2.11.4")
    implementation ("androidx.navigation:navigation-compose:2.7.5")
    implementation ("androidx.activity:activity-compose:1.7.2")
    implementation("com.google.accompanist:accompanist-permissions:0.28.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation ("androidx.compose.runtime:runtime-saveable:1.6.0")
    implementation("com.google.android.libraries.places:places:3.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")


    // Firebase cosas
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth:22.3.0")

    // HUella
    implementation("androidx.biometric:biometric:1.1.0")

    implementation("com.google.maps:google-maps-services:0.18.1") // Para llamadas HTTP si lo haces desde backend
    implementation ("com.google.firebase:firebase-firestore-ktx")
    implementation ("com.google.firebase:firebase-auth-ktx")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")
    implementation("com.google.firebase:firebase-messaging")

}

apply(plugin = "com.google.gms.google-services")