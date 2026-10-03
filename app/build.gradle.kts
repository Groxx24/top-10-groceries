import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.multiplatform)
    // Reads app/google-services.json (kept out of git) and sets up the default Firebase app.
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

kotlin {
    // Android only for now. Shared code already lives in commonMain, so an iOS target is a matter
    // of adding it here plus a Ktor Darwin engine and the Xcode shell.
    androidTarget {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.material3)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.gitlive.firebase.firestore)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(project.dependencies.platform(libs.firebase.bom))
            // Reports crashes by itself once the app starts; nothing in the code calls it.
            implementation(libs.firebase.crashlytics)
        }
    }
}

compose.resources {
    packageOfResClass = "com.top10.products.resources"
}

android {
    namespace = "com.top10.products"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.top10.products"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(libs.compose.ui.tooling)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
