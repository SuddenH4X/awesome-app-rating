plugins {
    id(libs.plugins.android.library.get().pluginId)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.mannodermaus.android.junit5)
    alias(libs.plugins.jlleitschuh.gradle.ktlint)
}

val version = "2.7.0"

android {
    namespace = "com.suddenh4x.ratingdialog"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
    }

    kotlin {
        jvmToolchain(libs.versions.jvmToolchain.get().toInt())
    }

    // Workaround for https://github.com/gradle-nexus/publish-plugin/issues/208
    publishing {
        singleVariant("release")
    }
}

dependencies {
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.google.material)

    implementation(libs.google.play.review)
    implementation(libs.google.play.review.ktx)

    testRuntimeOnly(libs.testing.junit.jupiter.engine)
    testImplementation(libs.testing.junit.jupiter.api)
    testImplementation(libs.testing.junit.jupiter.params)
    testImplementation(libs.testing.assertj.core)
    testImplementation(libs.testing.mockk)
}

afterEvaluate {
    tasks.named("lint") {
        dependsOn("ktlintCheck")
    }
}

ext {
    set("PUBLISH_GROUP_ID", "com.suddenh4x.ratingdialog")
    set("PUBLISH_VERSION", version)
    set("PUBLISH_ARTIFACT_ID", "awesome-app-rating")
    set(
        "PUBLISH_DESCRIPTION",
        "A highly customizable Android library providing a dialog, which asks the user to rate the app or give feedback. You can also use the library to show the Google in-app review easily under certain conditions.",
    )
    set("PUBLISH_URL", "https://github.com/SuddenH4X/awesome-app-rating")
    set("PUBLISH_LICENSE_NAME", "Apache License")
    set("PUBLISH_LICENSE_URL", "https://github.com/SuddenH4X/awesome-app-rating/blob/master/LICENSE")
    set("PUBLISH_DEVELOPER_ID", "SuddenH4X")
    set("PUBLISH_DEVELOPER_NAME", "Sascha Kühne")
    set("PUBLISH_DEVELOPER_EMAIL", "SuddenH4X@users.noreply.github.com")
    set("PUBLISH_SCM_CONNECTION", "scm:git:github.com/SuddenH4X/awesome-app-rating.git")
    set("PUBLISH_SCM_DEVELOPER_CONNECTION", "scm:git:ssh://github.com:SuddenH4X/awesome-app-rating.git")
    set("PUBLISH_SCM_URL", "https://github.com/SuddenH4X/awesome-app-rating/tree/master")
}

apply(from = "${rootProject.projectDir}/scripts/publish-module.gradle")
