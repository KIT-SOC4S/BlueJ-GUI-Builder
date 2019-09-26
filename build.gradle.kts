import org.openjfx.gradle.JavaFXOptions

plugins {
    application
    id("org.openjfx.javafxplugin") version "0.0.8"
    id("org.beryx.jlink") version "2.12.0"
    id("com.gradle.build-scan") version "2.1"
}

group = "jfxgui"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClassName = "bdl.Main"
}


repositories {
    mavenCentral()
}

configure<JavaFXOptions> {
    version = "11"
    modules("javafx.controls", "javafx.fxml", "javafx.swing", "javafx.web", "javafx.base", "javafx.media")
}


configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}

dependencies {
    testImplementation("junit", "junit", "4.12")
    testCompile("junit", "junit", "4.12")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}