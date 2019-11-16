import org.openjfx.gradle.JavaFXOptions


plugins {
    application
    java
    idea
    id("org.openjfx.javafxplugin") version "0.0.8"
    id("org.beryx.jlink") version "2.12.0"
    id ("com.github.johnrengelman.shadow") version "5.2.0"
}


group = "jfxgui"
version = "1.0-SNAPSHOT"
val javaVersion: JavaVersion by extra { JavaVersion.VERSION_11 }
val author = "Fabian Jackl"

repositories {
    mavenCentral()
}

application {
    applicationName = rootProject.name
    mainClassName = "bdl.BlueJConnector"
}


repositories {
    mavenCentral()
}
print(javaVersion.majorVersion)

configure<JavaFXOptions> {
    version = javaVersion.majorVersion
    modules("javafx.controls", "javafx.fxml", "javafx.swing", "javafx.web", "javafx.base", "javafx.media")
}


configure<JavaPluginConvention> {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

dependencies {
    testImplementation("junit", "junit", "4.12")
    testCompile("junit", "junit", "4.12")
    implementation("io.github.java-diff-utils:java-diff-utils:4.5")
    implementation("io.github.java-diff-utils:java-diff-utils-jgit:4.5")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = application.mainClassName
        attributes["Vendor"] = project.group
        attributes["Title"] = application.applicationName
        attributes["AppVersion"] = project.version
        attributes["CreatedBy"] = "Gradle ${gradle.gradleVersion}"
        attributes["OS"] = "${System.getProperty("os.name")} (${System.getProperty("os.version")})"
        attributes["BuildTarget"] = javaVersion
        attributes["JDK"] = System.getProperty("java.version")
        attributes["Author"] = author
    }
}

tasks.build {
    doLast {
        //exec { executable = "C:\\Program Files\\BlueJ\\BlueJ.exe" }
    }
}