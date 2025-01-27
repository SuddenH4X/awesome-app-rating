apply(from = "${rootDir}/scripts/publish-root.gradle")

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.jlleitschuh.gradle.ktlint) apply false
    alias(libs.plugins.gradle.nexus.publish)
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
