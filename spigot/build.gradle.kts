import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version("7.1.2")
}

group = "io.github.flameware.spigot"
version = "1.0.0alpha2"

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/central/")
}

dependencies {
    compileOnly("com.google.errorprone:error_prone_annotations:2.18.0")
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("net.kyori:adventure-text-minimessage:4.13.1")
    compileOnly("net.kyori:adventure-platform-bukkit:4.3.0")
    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    compileOnly("org.jetbrains:annotations:24.0.1")
    implementation(project(":common"))
}



tasks {
    shadowJar {
        archiveFileName.set("FlameWare-Spigot-${project.version}.jar")
        exclude("**/META-INF/**")
    }
}
