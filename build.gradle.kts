import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val exposedVersion = "0.31.1"

plugins {
    kotlin("jvm") version "1.5.0"
}

group = "de.skyslycer.dvyne"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://m2.dv8tion.net/releases")
        name = "m2-dv8tion"
    }
}

dependencies {
    testImplementation(kotlin("test-junit"))

    implementation("net.dv8tion:JDA:4.2.1_253")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.xerial:sqlite-jdbc:3.30.1")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}