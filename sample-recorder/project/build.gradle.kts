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
    implementation("io.github.microutils:kotlin-logging:1.7.7")
    implementation("ch.qos.logback:logback-classic:1.2.3")

    testImplementation("org.spekframework.spek2:spek-dsl-jvm:2.0.15")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:2.0.15")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.13")
}

tasks.test {
    systemProperty("spek2.execution.test.timeout", 0)
    useJUnitPlatform {
        includeEngines("spek2")
    }
}