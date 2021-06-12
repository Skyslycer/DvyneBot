import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val exposedVersion = "0.31.1"

plugins {
    kotlin("jvm") version "1.5.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "de.skyslycer.dvyne"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
    maven("https://jitpack.io")
}

dependencies {
//  implementation("net.dv8tion:JDA:4.2.1_253")
    implementation("com.github.DV8FromTheWorld:JDA:5885efc")

    implementation("org.slf4j", "slf4j-api", "1.7.30")
    implementation("ch.qos.logback", "logback-classic", "1.3.0-alpha5")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.xerial:sqlite-jdbc:3.30.1")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}