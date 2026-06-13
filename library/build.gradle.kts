import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id(libs.plugins.android.library.get().pluginId)
    alias(libs.plugins.mannodermaus.android.junit5)
    alias(libs.plugins.jlleitschuh.gradle.ktlint)
    alias(libs.plugins.vanniktech.maven.publish)
}

val version = "2.8.0"

kotlin {
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()

    coordinates("com.suddenh4x.ratingdialog", "awesome-app-rating", version)

    pom {
        name.set("awesome-app-rating")
        description.set(
            "A highly customizable Android library providing a dialog, which asks the user to rate the app or give " +
                "feedback. You can also use the library to show the Google in-app review easily under certain conditions.",
        )
        url.set("https://github.com/SuddenH4X/awesome-app-rating")
        licenses {
            license {
                name.set("Apache License")
                url.set("https://github.com/SuddenH4X/awesome-app-rating/blob/master/LICENSE")
            }
        }
        developers {
            developer {
                id.set("SuddenH4X")
                name.set("Sascha Kühne")
                email.set("SuddenH4X@users.noreply.github.com")
            }
        }
        scm {
            url.set("https://github.com/SuddenH4X/awesome-app-rating/tree/master")
            connection.set("scm:git:github.com/SuddenH4X/awesome-app-rating.git")
            developerConnection.set("scm:git:ssh://github.com:SuddenH4X/awesome-app-rating.git")
        }
    }
}
