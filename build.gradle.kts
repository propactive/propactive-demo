import org.gradle.api.tasks.wrapper.Wrapper.DistributionType.ALL
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.6.20"

    application
    id("java-library")
    id("io.github.propactive") version "DEV-SNAPSHOT"

    kotlin("jvm") version kotlinVersion
}

propactive {
    destination = layout.buildDirectory.dir("dist").get().asFile.absolutePath
    implementationClass = "propactive.demo.Properties"
}

application {
    val mainClassReference: String by project
    mainClass.set(mainClassReference)
}

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    val jupiterVersion: String by project
    val kotestVersion: String by project

    implementation("io.github.propactive:propactive-jvm:DEV-SNAPSHOT")
    testImplementation("org.junit.jupiter:junit-jupiter:$jupiterVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
}

tasks {
    test {
        useJUnitPlatform()
        testLogging { showStandardStreams = true }
    }

    wrapper {
        distributionType = ALL
    }

    withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "17"
    }
}