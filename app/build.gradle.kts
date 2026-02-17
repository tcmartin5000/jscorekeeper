import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Required for Compose post-Kotlin 2.0.0.
    alias(libs.plugins.compose.compiler)
    // Plugin for kotlinx serializable, a dependency of Compose Navigation.
    alias(libs.plugins.kotlinx.serialization)
    // Plugin for Kotlin Symbol Processing, used by Room.
    alias(libs.plugins.devtools.ksp)
    // For Room
    alias(libs.plugins.androidx.room)
}

android {
    namespace = "com.guillotine.jscorekeeper"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.guillotine.jscorekeeper"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // For handling save on low memory in the viewmodel.
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    // Navigation Compose.
    implementation(libs.androidx.navigation.compose)
    // Dependency for type-safe Navigation Compose.
    implementation(libs.kotlinx.serialization.core)
    // Dependency for proto DataStore.
    implementation(libs.kotlinx.serialization.json)
    // Used just for Proto DataStore.
    implementation(libs.kotlinx.collections.immutable)
    // For the DataStore, the API that replaces SharedPreferences, to save games to resume later.
    implementation(libs.androidx.datastore)
    // For saving during onDestroy and guaranteeing it will complete.
    implementation(libs.androidx.work.runtime.ktx)
    // For saving statistics to a proper database.
    implementation(libs.androidx.room.runtime)
    // Legacy icons, because they work fine for my purpose and I don't feel like upgrading rn.
    implementation(libs.compose.material.icons)
    implementation(libs.androidx.paging.common.android)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.room.paging)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.adaptive.android)
    implementation(libs.androidx.window.core)
    ksp(libs.androidx.room.compiler)
    // For async Room (necessary seemingly, not sure why this is separate?)
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}