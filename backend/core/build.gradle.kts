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

    // MVEL for rule evaluation
    implementation(Libraries.mvel)

    // Caffeine cache
    implementation(Libraries.caffeine)

    // Dropwizard
    implementation(Libraries.dropwizard_core)

    // Mockk
    testImplementation(Libraries.mockk)

    // Junit
    testImplementation(Libraries.junit_jupiter_api)
    testImplementation(Libraries.junit_jupiter_params)
    testRuntimeOnly(Libraries.junit_jupiter_engine)
}

