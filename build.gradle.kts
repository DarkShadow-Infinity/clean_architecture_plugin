import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.models.ProductRelease
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val pluginName: String = providers.gradleProperty("pluginName")
    .orElse("Flutter Clean Architecture Helper")
    .get()
val pluginGroup: String = providers.gradleProperty("pluginGroup")
    .orElse("org.clean.architecture")
    .get()
val pluginVersion: String = providers.gradleProperty("pluginVersion")
    .orElse("1.1.0")
    .get()
val ideaVersion: String = providers.gradleProperty("ideaVersion")
    .orElse("2026.1.4")
    .get()
val vendorName: String = providers.gradleProperty("vendorName")
    .orElse("NeoShadow")
    .get()
val vendorEmail: String = providers.gradleProperty("vendorEmail")
    .orElse("diego.palomaresgarcia@gmail.com")
    .get()
val vendorUrl: String = providers.gradleProperty("vendorUrl")
    .orElse("https://github.com/DarkShadow-Infinity")
    .get()
val publishChannels: String = providers.gradleProperty("publishChannels")
    .orElse("stable")
    .get()
val certificateChainValue: String? = providers.gradleProperty("certificateChain").orNull
val privateKeyValue: String? = providers.gradleProperty("privateKey").orNull
val privateKeyPasswordValue: String? = providers.gradleProperty("privateKeyPassword").orNull
val publishTokenValue: String? = providers.gradleProperty("publishToken").orNull

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
// Android Studio 2026.1 (261) is the first supported runtime for the Java 21-era platform line.
val sinceBuildValue: String = System.getProperty("SINCE_BUILD") ?: "261"
val untilBuildValue: String = System.getProperty("UNTIL_BUILD") ?: "262.*"

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.4.20"
    id("org.jetbrains.intellij.platform") version "2.19.0"
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
        intellijIdea(ideaVersion) {
            useInstaller = false
        }
        bundledPlugin("com.intellij.java")
    }
    testImplementation(kotlin("test"))
    testImplementation("junit:junit:4.13.2")
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
                <li>Improved compatibility with Android Studio 2026.2+ (build 262+)</li>
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
        certificateChain.set(certificateChainValue ?: System.getProperty("CERTIFICATE_CHAIN"))
        privateKey.set(privateKeyValue ?: System.getProperty("PRIVATE_KEY"))
        password.set(privateKeyPasswordValue ?: System.getProperty("PRIVATE_KEY_PASSWORD"))
    }

    publishing {
        token.set(publishTokenValue ?: System.getProperty("PUBLISH_TOKEN"))
        channels.set(listOf(publishChannels)) // Opcional si usas canales como "stable", "eap"
    }

    pluginVerification {
        ides {
            select {
                types = listOf(IntelliJPlatformType.AndroidStudio)
                channels = listOf(ProductRelease.Channel.RELEASE, ProductRelease.Channel.PATCH)
                sinceBuild = sinceBuildValue
                untilBuild = untilBuildValue
            }
        }
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}
