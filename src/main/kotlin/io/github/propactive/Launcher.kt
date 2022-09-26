package io.github.propactive

import io.github.propactive.demo.Outputer
import io.github.propactive.demo.Properties.portPropertyKey
import io.github.propactive.demo.Properties.primePropertyKey
import io.github.propactive.demo.Properties.prodOnlyStringPropertyKey
import io.github.propactive.demo.Properties.timoutInMsPropertyKey
import io.github.propactive.demo.Properties.urlPropertyKey
import io.github.propactive.demo.Properties.uuidPropertyKey
import java.io.File
import java.io.FileInputStream
import java.net.URL
import java.util.*

object Launcher {
    internal const val ENVIRONMENT_KEY = "ENVIRONMENT"

    internal const val APPLICATION_PROPERTIES_FILE_NAME = "application.properties"
    internal const val APPLICATION_PROPERTIES_CONFIG_DIR_PATH_KEY = "properties.config.path"

    private val properties by lazy { Properties().loadFrom(APPLICATION_PROPERTIES_CONFIG_DIR_PATH_KEY) }

    @JvmStatic
    fun main(args: Array<String>) {
        Outputer.displayCollectedProperties(
            System.getenv(ENVIRONMENT_KEY),
            properties.getProperty(prodOnlyStringPropertyKey, ""),
            properties.getProperty(portPropertyKey).toInt(),
            properties.getProperty(urlPropertyKey).let(::URL),
            properties.getProperty(timoutInMsPropertyKey).toInt(),
            properties.getProperty(uuidPropertyKey).let(UUID::fromString),
            properties.getProperty(primePropertyKey).toInt(),
        ).let(::println)
    }

    internal fun Properties.loadFrom(configPath: String) = System.getProperty(configPath)
        .apply { check(!isNullOrBlank()) { "Could not load properties from -D$configPath as it's not set or has blank value" } }
        .split(File.pathSeparator)
        .map { configDir -> "$configDir${File.separator}$APPLICATION_PROPERTIES_FILE_NAME" }
        .map(::FileInputStream)
        .fold(this) { acc, input -> acc.apply { load(input) } }
}