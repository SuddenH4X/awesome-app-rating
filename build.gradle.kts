plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jlleitschuh.gradle.ktlint) apply false
    alias(libs.plugins.kotlin.compose.compiler) apply false
    alias(libs.plugins.vanniktech.maven.publish) apply false
}

buildscript {
    repositories {
        google()
        maven(url = "https://plugins.gradle.org/m2/")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
