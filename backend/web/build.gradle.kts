plugins {
    kotlin("jvm")
}

dependencies {
    // API module dependency
    implementation(project(":api"))

    // Kotlin libs
    implementation(kotlin("stdlib"))

    // Logging
    implementation(Libraries.microutils_logging)

    // JAX-RS annotations for REST controllers
    implementation(Libraries.dropwizard_core)

    // Junit
    testImplementation(Libraries.junit_jupiter_api)
    testRuntimeOnly(Libraries.junit_jupiter_engine)
} 