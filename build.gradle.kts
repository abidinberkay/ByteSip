// ByteSip — IntelliJ plugin build script (Kotlin DSL)
// Uses the IntelliJ Platform Gradle Plugin 2.x and targets IntelliJ 2024.2+ with Java 21.

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_21.toString()
    targetCompatibility = JavaVersion.VERSION_21.toString()
}

plugins {
    kotlin("jvm") version "2.0.21"
    id("org.jetbrains.intellij.platform") version "2.1.0"
}

group = "com.bytesip"
version = "1.0.1"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        // Target IntelliJ IDEA Community 2024.2 (uses JBR 21).
        intellijIdeaCommunity("2024.2")
        instrumentationTools()
        pluginVerifier()
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            // Compatibility range — 242 = 2024.2, blank "untilBuild" keeps it open-ended.
            sinceBuild = "242"
            untilBuild = provider { null }
        }
    }

}

kotlin {
    jvmToolchain(21)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks {
    named("instrumentCode") {
        enabled = false
    }
}
