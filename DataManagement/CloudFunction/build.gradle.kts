plugins {
    kotlin("jvm")
    id("com.google.cloud.tools.jib") version "3.1.4" // Per creare immagini Docker
}

group = "it.umbria.regione"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.cloud.functions:functions-framework-api:1.0.4")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.ktor:ktor-server-core:1.5.4")
    implementation("io.ktor:ktor-server-netty:1.5.4")
    implementation(project(":PNRR"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

// URL https://us-central1-espertipnrr-421612.cloudfunctions.net/importPNRR
