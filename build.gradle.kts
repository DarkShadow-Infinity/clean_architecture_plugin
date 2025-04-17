val pluginGroup: String by project
val pluginName: String by project
val pluginVersion: String by project
val pluginDescription: String by project

val ideaVersion: String by project
val targetIDE: String by project

val publishToken: String by project
val publishChannels: String by project

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij") version "1.17.4"
}

group = pluginGroup
version = pluginVersion

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set(ideaVersion)
    type.set(targetIDE) // Target IDE Platform
    // Plugin Android
    plugins.set(listOf("com.intellij.java"))
}

tasks {

    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("243")
        changeNotes = """
            <h1>Changelog</h1>
            <h2>v1.0.0</h2>
            <h3>Release Notes – Flutter Clean Architecture Helper</h3>
            <ul>
                <li>First stable release of the plugin</li>
                <li>Generates a full-featured Flutter Clean Architecture folder structure</li>
                <li>Supports optional root folder naming</li>
                <li>Supports optional split for local and remote data sources</li>
            </ul>
       """
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

}

