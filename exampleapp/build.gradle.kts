plugins {
    id(libs.plugins.android.application.get().pluginId)
    alias(libs.plugins.jlleitschuh.gradle.ktlint)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose.compiler)
}

android {
    namespace = "com.suddenh4x.ratingdialog.exampleapp"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "com.suddenh4x.ratingdialog.exampleapp"
        minSdk = 21
        targetSdk = libs.versions.targetSdk.get().toInt()
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
        jvmToolchain(libs.versions.jvmToolchain.get().toInt())
    }
}

dependencies {
    implementation(project(":library"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.google.material)
}

afterEvaluate {
    tasks.named("lint") {
        dependsOn("ktlintCheck")
    }
}
