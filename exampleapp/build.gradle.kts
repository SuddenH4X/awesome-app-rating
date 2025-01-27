plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "com.suddenh4x.ratingdialog.exampleapp"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.suddenh4x.ratingdialog.exampleapp"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    implementation(project(":library"))
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation("androidx.compose.ui:ui-tooling:1.7.6")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("com.google.android.material:material:1.12.0")
}

afterEvaluate {
    tasks.named("lint") {
        dependsOn("ktlintCheck")
    }
}
