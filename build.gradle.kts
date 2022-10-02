import org.gradle.api.tasks.wrapper.Wrapper.DistributionType.ALL
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.6.20"
    val propactiveVersion = "1.1.0"

    application

    id("java-library")
    id("io.github.propactive") version propactiveVersion

    kotlin("jvm") version kotlinVersion
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
}

version = System.getenv("VERSION") ?: "DEV-SNAPSHOT"

propactive {
    implementationClass = "io.github.propactive.demo.Properties"
}

application {
    val mainClassReference: String by project
    val propertiesConfigPath = layout.buildDirectory.dir("properties").get().asFile.absolutePath

    mainClass.set(mainClassReference)

    applicationDefaultJvmArgs = listOf(
        "-Xmx512m",
        "-XX:MaxMetaspaceSize=256m",
        "-Dproperties.config.path=$propertiesConfigPath"
    )

    tasks["run"]
        .dependsOn("generateApplicationProperties")
}

repositories {
    mavenCentral()
}

dependencies {
    val jupiterVersion: String by project
    val kotestVersion: String by project
    val propactiveVersion: String by project

    implementation("io.github.propactive:propactive-jvm:$propactiveVersion")
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

    jar {
        val mainClassReference: String by project

        manifest.apply {
            attributes["Implementation-Title"] = project.name
            attributes["Implementation-Version"] = project.version
            attributes["Implementation-Vendor"] = "Propactive"
            attributes["Main-Class"] = mainClassReference
        }

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(sourceSets.main.get().output)
        dependsOn(configurations.runtimeClasspath)
        from({
            configurations.runtimeClasspath.get()
                .filter { it.name.endsWith("jar") }
                .map { zipTree(it) }
        })
    }

    withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "17"
    }

    ktlint {
        verbose.set(true)
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
