plugins {
    kotlin("jvm")
}

group = "org.light"
version = "1.0-SNAPSHOT"


dependencies {
    // Kotlin libs
    implementation(kotlin("stdlib"))

    // Jackson for JSON annotations
    implementation(Libraries.jackson_kotlin)

    testImplementation(kotlin("test"))
}
