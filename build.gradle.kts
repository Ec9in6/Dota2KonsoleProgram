plugins {
    kotlin("jvm") version "2.1.10" //
    kotlin("plugin.serialization") version "2.1.10"
    id("org.jetbrains.compose") version "1.6.11"
    kotlin("plugin.compose") version "2.1.10"
}

group = "me.user"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)

    val ktorVersion = "3.0.0"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.4.11")
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "21"
    }
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = "21"
    targetCompatibility = "21"
}