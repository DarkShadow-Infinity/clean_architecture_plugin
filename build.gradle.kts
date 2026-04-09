val pluginName: String by project
val pluginGroup: String by project
val pluginVersion: String by project
val ideaVersion: String by project
val vendorName: String by project
val vendorEmail: String by project
val vendorUrl: String by project
val publishChannels: String by project
val certificateChain: String by project
val privateKey: String by project
val privateKeyPassword: String by project
val publishToken: String by project

// Load .env file if it exists
val envFile = file(".env")
if (envFile.exists()) {
    envFile.readLines().forEach { line ->
        if (line.isNotBlank() && !line.startsWith("#")) {
            val parts = line.split("=", limit = 2)
            if (parts.size == 2) {
                val key = parts[0].trim()
                val value = parts[1].trim().removeSurrounding("\"")
                System.setProperty(key, value)
            }
        }
    }
}

// Read build compatibility from .env or use defaults
val sinceBuildValue: String = System.getProperty("SINCE_BUILD") ?: "233"
val untilBuildValue: String = System.getProperty("UNTIL_BUILD") ?: "252.*"

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij.platform") version "2.5.0"
}

group = pluginGroup
version = pluginVersion

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html#setting-up-intellij-platform
dependencies {
    intellijPlatform {
        intellijIdeaCommunity(ideaVersion, useInstaller = false)
        bundledPlugin("com.intellij.java")
    }
}

intellijPlatform {
    pluginConfiguration {
        id = pluginGroup
        name = pluginName
        version = pluginVersion
        changeNotes.set("""
            <h1>Changelog</h1>
            <h2>v1.1.0</h2>
            <h3>New Features</h3>
            <ul>
                <li>Added Architecture Style selector with 3 presets: Default, Core/UI/Widgets, and Custom</li>
                <li>New Core/UI/Widgets preset creates core/, ui/, and top-level widgets/ folder</li>
                <li>Custom option reads layer names from Settings > Tools > Clean Architecture</li>
                <li>Build compatibility now reads from .env file (SINCE_BUILD, UNTIL_BUILD)</li>
                <li>Improved compatibility with Android Studio 2025.3+ (build 253+)</li>
            </ul>
            <h2>v1.0.1</h2>
            <ul>
                <li>Updated UNTIL_BUILD for compatibility with newer Android Studio versions</li>
                <li>Build compatibility now reads from .env file (SINCE_BUILD, UNTIL_BUILD)</li>
            </ul>
            <h2>v1.0.0</h2>
            <h3>Release Notes – Flutter Clean Architecture Helper</h3>
            <ul>
                <li>First stable release of the plugin</li>
                <li>Generates a full-featured Flutter Clean Architecture folder structure</li>
                <li>Supports optional root folder naming</li>
                <li>Supports optional split for local and remote data sources</li>
            </ul>
            """.trimIndent())
        ideaVersion {
            sinceBuild.set(sinceBuildValue)
            untilBuild.set(untilBuildValue)
        }
        vendor {
            name = vendorName
            email = vendorEmail
            url = vendorUrl
        }
    }

    signing {
        certificateChain.set(System.getProperty("CERTIFICATE_CHAIN"))
        privateKey.set(System.getProperty("PRIVATE_KEY"))
        password.set(System.getProperty("PRIVATE_KEY_PASSWORD"))
    }

    publishing {
        token.set(System.getProperty("PUBLISH_TOKEN"))
        channels.set(listOf(publishChannels)) // Opcional si usas canales como "stable", "eap"
    }
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}

