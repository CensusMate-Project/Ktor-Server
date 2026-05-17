plugins {
    kotlin("jvm") version "2.3.20"
    id("io.ktor.plugin") version "3.1.3"
    kotlin("plugin.serialization") version "2.2.10"
}

group = "org.censusmate"
version = "dev-0.0.1"

application {
    mainClass.set("org.censusmate.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    // Ktor Server and related dependencies
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-status-pages-jvm")
    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.ktor:ktor-server-auth-jwt-jvm")
    implementation("io.ktor:ktor-server-auth-jwt")
    implementation("io.ktor:ktor-server-cors-jvm")

    // PostgreSQL driver and Exposed ORM
    implementation("org.jetbrains.exposed:exposed-core:0.55.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.55.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.55.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.55.0")
    implementation("org.postgresql:postgresql:42.7.4")

    // HikariCP for connection pooling
    implementation("com.zaxxer:HikariCP:6.0.0")

    // OpenAPI
    implementation("io.github.smiley4:ktor-openapi:5.7.0")
    implementation("io.github.smiley4:schema-kenerator-core:2.7.2")
    implementation("io.github.smiley4:schema-kenerator-swagger:2.7.2")
    implementation("io.github.smiley4:schema-kenerator-serialization:2.7.2")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.6")

    // Security and password hashing
    implementation("at.favre.lib:bcrypt:0.10.2")

    // DaData API client
    implementation("ru.marisov:ktor-dadata-client:3.1.1")

    // Ktor client
    implementation("io.ktor:ktor-client-core-jvm")
    implementation("io.ktor:ktor-client-cio-jvm")
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}