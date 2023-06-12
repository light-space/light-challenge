import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    base
    application
}

allprojects {
    group = "org.light"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "11"
    }
}

tasks.named<JavaExec>("run") {
    group = "application"
    description = "Run the application"
    classpath = sourceSets["main"].runtimeClasspath
    args = mutableListOf("server", "config.yml")
}

application {
    mainClass.set("org.light.challenge.AppKt")
}

dependencies {
    // Kotlin libs
    implementation(kotlin("stdlib"))

    // Logging
    implementation(Libraries.microutils_logging)

    // Dropwizard
    implementation(Libraries.dropwizard_core)
    implementation(Libraries.jackson_kotlin)

    // Mockk
    testImplementation(Libraries.mockk)

    // Junit
    testImplementation(Libraries.junit_jupiter_api)
    testRuntimeOnly(Libraries.junit_jupiter_engine)
}



