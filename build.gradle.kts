apply(plugin = "io.github.gradle-nexus.publish-plugin")
apply(from = "${rootDir}/scripts/publish-root.gradle")

buildscript {
    val kotlinVersion by extra("1.9.10")
    repositories {
        google()
        maven(url = "https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.8.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("de.mannodermaus.gradle.plugins:android-junit5:1.11.2.0")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:12.1.2")
        classpath("io.github.gradle-nexus:publish-plugin:1.3.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
