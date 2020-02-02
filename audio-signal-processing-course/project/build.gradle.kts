import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "1.3.61"
    application
}

application {
    mainClassName = findProperty("mainClass")?.toString() ?: "NoClassDefined"
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

apply {
    plugin("kotlin")
}

repositories {
    jcenter()
    mavenCentral()
    // WaveBeans is in separate maven repository
    maven {
        name = "Bintray WaveBeans"
        url = uri("https://dl.bintray.com/wavebeans/wavebeans")
    }
}

dependencies {
    // required kotlin dependecies
    implementation(kotlin("stdlib-jdk8"))

    // both wavebeans dependecies
    implementation("io.wavebeans:exe:0.0.1")
    implementation("io.wavebeans:lib:0.0.1")

    // te see some log output in `logs/` folder in case of error
    implementation("ch.qos.logback:logback-classic:1.2.3")
}
