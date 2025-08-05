plugins {
    kotlin("jvm")
    application
}

dependencies {
    // Module dependencies
    implementation(project(":api"))
    implementation(project(":core"))
    implementation(project(":web"))

    // Kotlin libs
    implementation(kotlin("stdlib"))

    // Logging
    implementation(Libraries.microutils_logging)

    // Dropwizard (includes its own logging)
    implementation(Libraries.dropwizard_core)
    implementation(Libraries.jackson_kotlin)

    // Junit
    testImplementation(Libraries.junit_jupiter_api)
    testRuntimeOnly(Libraries.junit_jupiter_engine)
}

application {
    mainClass.set("org.light.challenge.app.AppKt")
}

tasks.named<JavaExec>("run") {
    group = "application"
    description = "Run the application"
    classpath = sourceSets["main"].runtimeClasspath
    args = mutableListOf("server", "config.yml")
} 