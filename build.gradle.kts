import org.openjfx.gradle.JavaFXOptions

plugins {
    application
    id("org.openjfx.javafxplugin") version "0.0.8"
    id("org.beryx.jlink") version "2.12.0"
    id("com.gradle.build-scan") version "2.1"
}

group = "edu.kit.ipd.soc4s"
version = "1.0-SNAPSHOT"
val bluejdir = "bluejsrc"
val jfxsdk = File(bluejdir, "jfx")
val bluejsrcurl = "https://www.bluej.org/download/files/source/BlueJ-source-421.zip"
val ojfxsdkurl = "https://download2.gluonhq.com/openjfx/13/openjfx-13_windows-x64_bin-sdk.zip"

repositories {
    mavenCentral()
}

application {
    mainClassName = "di.blueJLink.MenuExtensionGUIDesignerFXMAIN"
}


repositories {
    mavenCentral()
}

configure<JavaFXOptions> {
    version = "13"
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
    implementation("org.openjfx", "javafx-base", javafx.version, classifier = "win")
    implementation("org.openjfx", "javafx-controls", javafx.version, classifier = "win")
    implementation("org.openjfx", "javafx-media", javafx.version, classifier = "win")
    implementation("org.openjfx", "javafx-swing", javafx.version, classifier = "win")
    implementation("org.openjfx", "javafx-web", javafx.version, classifier = "win")
    implementation("org.openjfx", "javafx-fxml", javafx.version, classifier = "win")
    implementation("org.openjfx", "javafx-graphics", javafx.version, classifier = "win")
    implementation("org.openjfx", "javafx-graphics", javafx.version, classifier = "linux")
    implementation("org.openjfx", "javafx-graphics", javafx.version, classifier = "mac")
}

File(bluejdir).mkdirs()
jfxsdk.mkdirs()

tasks {
    register("ameise") {
        doLast {
            ant.properties["buildDir"] = bluejdir
            ant.properties["build_java_home"] = System.getProperty("java.home")
            ant.properties["java_command_dir_name"] = "bin"
            ant.properties["openjfx_path"] = "jfxsdk"
            ant.withGroovyBuilder {
                "get"("src" to bluejsrcurl, "dest" to "${bluejdir}/src.zip", "verbose" to true, "skipexisting" to true)
                "get"("src" to ojfxsdkurl, "dest" to "${bluejdir}/sdk.zip", "verbose" to true, "skipexisting" to true)
                "unzip"("src" to "${bluejdir}/src.zip", "dest" to bluejdir)
                "unzip"("src" to "${bluejdir}/sdk.zip", "dest" to File(bluejdir, "tmp"))
                File(bluejdir, "tmp").listFiles().first { it.isDirectory }.renameTo(File(bluejdir, "jfxsdk"))
                "ant"("dir" to bluejdir)
            }
            File(bluejdir, "lib/bluejext.jar").copyTo(File("libs/bluejextcompiled.jar"))
        }
    }
}