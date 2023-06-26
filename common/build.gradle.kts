plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version("7.1.2")
}

group = "io.github.flameware.common"
version = "1.0.0"

repositories {
    mavenCentral()
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:1.8.21")
    compileOnly("org.jetbrains.kotlin:kotlin-reflect:1.8.21")
    compileOnly("com.google.errorprone:error_prone_annotations:2.18.0")
    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    compileOnly("org.jetbrains:annotations:24.0.1")
}

tasks {
    shadowJar {
        archiveFileName.set("FlameWare-common-${project.version}.jar")
        exclude("**/META-INF/**")
    }
}
