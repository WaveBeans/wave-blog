plugins {
    kotlin("jvm") version "1.4.21"
}
repositories {
    jcenter()
    maven {
        name = "Bintray WaveBeans"
        url = uri("https://dl.bintray.com/wavebeans/wavebeans")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    implementation("io.wavebeans:exe:0.3.0")
    implementation("io.wavebeans:lib:0.3.0")
}
