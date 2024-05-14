plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.mannodermaus.junit5)
}

android {
    namespace = "id.slava.nt.cabifymobilechallengeapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "id.slava.nt.cabifymobilechallengeapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
        kotlinCompilerExtensionVersion = "1.5.5"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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
    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Room
    implementation (libs.androidx.room.runtime)
    implementation (libs.androidx.room.ktx)
    // KSP offers a number of advantages over Kapt for Room database development,
    // including faster build times, better error reporting, and improved nullability handling.
    ksp (libs.androidx.room.compiler)

    // serialization-json for Retrofit
    implementation (libs.gson)

    // Retrofit
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.okhttp)
    implementation (libs.logging.interceptor)

    //DI Koin
    implementation (libs.koin.android)
    implementation (libs.koin.androidx.navigation)
    implementation (libs.koin.androidx.compose)
    // Koin testing tools
    testImplementation (libs.koin.test)
    // Needed JUnit version
    testImplementation (libs.koin.test.junit4)

    testImplementation (libs.junit.jupiter.api)
    testRuntimeOnly (libs.junit.jupiter.engine)
    testImplementation (libs.junit.jupiter.params)


    testImplementation(libs.assertk.jvm)
    testImplementation(libs.mockwebserver)

    implementation(libs.kotlinx.coroutines.test)

// Mockito
    testImplementation (libs.mockito.core)
    androidTestImplementation (libs.mockito.android)
    testImplementation (libs.mockito.inline)
    testImplementation (libs.mockito.kotlin)

    // testing flows
    testImplementation (libs.turbine)
    androidTestImplementation (libs.turbine)
}