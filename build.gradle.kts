import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    application
}

group = "org.isen.carburmap"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://josm.openstreetmap.de/nexus/content/repositories/releases/")
}

dependencies {
    //map
    implementation("org.openstreetmap.jmapviewer:jmapviewer:2.16")
    implementation("com.graphhopper:directions-api-client-hc:6.2")

    //Kotlin
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.22")
    implementation("com.google.guava:guava:31.0.1-jre")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.20")

    // LOG4J
    implementation("org.apache.logging.log4j:log4j-api:2.19.0")
    implementation("org.apache.logging.log4j:log4j-core:2.19.0")
    implementation("org.apache.logging.log4j:log4j-api-kotlin:1.2.0")

    // FUEL API
    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation("com.github.kittinunf.fuel:fuel-coroutines:2.3.1")
    implementation("com.github.kittinunf.fuel:fuel-gson:2.3.1")

    // GSON API
    implementation("com.google.code.gson:gson:2.10")

    // XML Jackson API
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.13.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("org.isen.carburmap.MainKt")
}

tasks{
    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources")) // We need this for Gradle optimization to work
        archiveClassifier.set("standalone") // Naming the jar
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) } // Provided we set it up in the application plugin configuration
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) } +
                sourcesMain.output
        from(contents)
    }
    build {
        dependsOn(fatJar) // Trigger fat jar creation during build
    }
}