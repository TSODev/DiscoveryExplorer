plugins {
    kotlin("jvm") version "1.8.21"
    application
}

group = "fr.tsodev"
version = "1.0.2"

repositories {
    google()
    mavenCentral()

    dependencies {
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.google.code.gson:gson:2.9.1")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")
        implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
        implementation("com.xenomachina:kotlin-argparser:2.0.7")
        implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.9.1")
        implementation("com.github.librepdf:openpdf:1.3.30")
        implementation("io.github.oshai:kotlin-logging-jvm:4.0.0")
        implementation("org.slf4j:slf4j-reload4j:2.0.7")
        testImplementation(kotlin("test"))
    }

    tasks.test {
        useJUnitPlatform()
    }

    kotlin {
        jvmToolchain(17)
    }

    application {
        mainClass.set("MainKt")
    }
}
