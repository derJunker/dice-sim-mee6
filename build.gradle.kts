plugins {
    kotlin("jvm") version "2.1.10"
}

group = "pogo.dice.sim"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://packages.jetbrains.team/maven/p/kds/kotlin-ds-maven")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kandy-lets-plot:0.8.0")
    implementation("org.jetbrains.kotlinx:kotlin-statistics-jvm:0.4.0")
}

kotlin {
    jvmToolchain(21)
}