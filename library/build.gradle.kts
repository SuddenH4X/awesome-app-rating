plugins {
    id("com.android.library")
    id("kotlin-android")
    id("de.mannodermaus.android-junit5")
}

val version = "2.7.0"

android {
    namespace = "com.suddenh4x.ratingdialog"
    compileSdk = 35
    defaultConfig {
        minSdk = 14
        targetSdk = 35
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
        jvmToolchain(17)
    }

    // Workaround for https://github.com/gradle-nexus/publish-plugin/issues/208
    publishing {
        singleVariant("release")
    }
}

val junitVersion = "5.11.4"

dependencies {
    implementation("androidx.annotation:annotation:1.9.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("com.google.android.material:material:1.12.0")

    implementation("com.google.android.play:review:2.0.2")
    implementation("com.google.android.play:review-ktx:2.0.2")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testImplementation("org.assertj:assertj-core:3.26.3")
    testImplementation("io.mockk:mockk:1.13.14")
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
    set("PUBLISH_DESCRIPTION", "A highly customizable Android library providing a dialog, which asks the user to rate the app or give feedback. You can also use the library to show the Google in-app review easily under certain conditions.")
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
