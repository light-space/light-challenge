plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.2.0"
}

dependencies {
    // API module dependency
    implementation(project(":api"))

    // Kotlin libs
    implementation(kotlin("stdlib"))

    // Logging
    implementation(Libraries.microutils_logging)

    // Spring SpEL for rule evaluation
    implementation(Libraries.spring_expression)

    // Caffeine cache
    implementation(Libraries.caffeine)

    // Dropwizard (includes its own logging)
    implementation(Libraries.dropwizard_core)

    // Koog AI for LLM agents (exclude conflicting SLF4J versions)
    implementation(Libraries.koog_agents) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    // Kotlinx Serialization for structured data
    implementation(Libraries.kotlinx_serialization_json)

    // Coroutines for async operations
    implementation(Libraries.kotlinx_coroutines_core)

    // Mockk
    testImplementation(Libraries.mockk)

    // Junit
    testImplementation(Libraries.junit_jupiter_api)
    testImplementation(Libraries.junit_jupiter_params)
    testRuntimeOnly(Libraries.junit_jupiter_engine)
}

