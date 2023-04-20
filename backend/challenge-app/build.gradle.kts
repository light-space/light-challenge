plugins {
    kotlin("jvm")
    application
}

tasks.named<JavaExec>("run") {
    group = "application"
    description = "Run the application"
    classpath = sourceSets["main"].runtimeClasspath
    args = mutableListOf("server", "config.yml")
}

application {
    mainClass.set("org.light.challenge.app.AppKt")
}

dependencies {
    implementation(project(":challenge-core"))
    implementation(project(":challenge-data"))

    // Kotlin libs
    implementation(kotlin("stdlib"))

    // Logging
    implementation(Libraries.log4j_over_slf4j)
    implementation(Libraries.microutils_logging)

    // Dropwizard
    implementation(Libraries.dropwizard_core)
    implementation(Libraries.jackson_kotlin)
}
