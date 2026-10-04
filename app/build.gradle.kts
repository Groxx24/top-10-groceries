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
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

kotlin {
    // Android only for now. Shared code already lives in commonMain, so an iOS target is a matter
    // of adding it here plus a Ktor Darwin engine and the Xcode shell.
    androidTarget {
        // 17 because GitLive's Firestore inline functions are built for 17 and cannot be inlined into 11.
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
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
            implementation(libs.kotlincrypto.hmac.sha2)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.kotlinx.datetime)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.ktor.client.okhttp)
            implementation(project.dependencies.platform(libs.firebase.bom))
            // Reports crashes by itself once the app starts; nothing in the code calls it.
            implementation(libs.firebase.crashlytics)
            // The banner under the store list, and the consent form shown before any ad is requested.
            implementation(libs.play.services.ads)
            implementation(libs.ump)
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

compose.resources {
    packageOfResClass = "com.top10.deals.resources"
}

android {
    namespace = "com.top10.deals"
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

    buildFeatures {
        // BuildConfig.DEBUG decides whether the debug screen can be reached.
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // Room generates code per target, so its compiler is added to each one.
    add("kspAndroid", libs.androidx.room.compiler)
    debugImplementation(libs.compose.ui.tooling)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
