package io.github.propactive

import io.github.propactive.Launcher.APPLICATION_PROPERTIES_CONFIG_DIR_PATH_KEY
import io.github.propactive.Launcher.APPLICATION_PROPERTIES_FILE_NAME
import io.github.propactive.Launcher.ENVIRONMENT_KEY
import io.github.propactive.Launcher.loadFrom
import io.github.propactive.demo.Properties.portPropertyKey
import io.github.propactive.demo.Properties.primePropertyKey
import io.github.propactive.demo.Properties.prodOnlyStringPropertyKey
import io.github.propactive.demo.Properties.timoutInMsPropertyKey
import io.github.propactive.demo.Properties.urlPropertyKey
import io.github.propactive.demo.Properties.uuidPropertyKey
import io.github.propactive.demo.shared.PropertiesTestCase
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.extensions.system.OverrideMode.SetOrOverride
import io.kotest.extensions.system.withEnvironment
import io.kotest.extensions.system.withSystemProperty
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.io.File
import java.util.*

class LauncherTest : PropertiesTestCase() {
    @Nested
    @TestInstance(PER_CLASS)
    inner class Main {
        @Test
        fun shouldBeAbleToLaunchAppWhenExpectedPropertiesAreProvided() {
            withEnvironment(ENVIRONMENT_KEY, environmentValue, SetOrOverride) {
                val propertiesFile = createPropertiesFile {
                    it.writeText(
                        """
                        $prodOnlyStringPropertyKey=$stringPropertyValue
                        $portPropertyKey=$portPropertyValue
                        $urlPropertyKey=$urlPropertyValue
                        $timoutInMsPropertyKey=$timoutInMsPropertyValue
                        $uuidPropertyKey=$uuidPropertyValue
                        $primePropertyKey=$customPropertyValue
                        """.trimIndent()
                    )
                }

                withSystemProperty(APPLICATION_PROPERTIES_CONFIG_DIR_PATH_KEY, propertiesFile.canonicalFile.parent) {
                    Launcher.main(emptyArray())
                }
            }
        }
    }

    @Nested
    @TestInstance(PER_CLASS)
    inner class PropertiesLoader {
        @ParameterizedTest
        @CsvSource("blank", "null")
        fun shouldThrowIllegalStateExceptionWhenConfigPathResolveToBlankOrNull(value: String) {
            withSystemProperty(APPLICATION_PROPERTIES_CONFIG_DIR_PATH_KEY, if (value == "blank") "" else null, SetOrOverride) {
                shouldThrow<IllegalStateException> {
                    Properties().loadFrom(APPLICATION_PROPERTIES_CONFIG_DIR_PATH_KEY)
                }.message shouldBe "Could not load properties from -D$APPLICATION_PROPERTIES_CONFIG_DIR_PATH_KEY as it's not set or has blank value"
            }
        }

        @Test
        fun shouldBeAbleToLoadPropertiesFromValidPropertiesFile() {
            val propertiesFile = createPropertiesFile { it.writeText("$portPropertyKey=42") }

            withSystemProperty(APPLICATION_PROPERTIES_CONFIG_DIR_PATH_KEY, propertiesFile.canonicalFile.parent, SetOrOverride) {
                Properties().loadFrom(APPLICATION_PROPERTIES_CONFIG_DIR_PATH_KEY)
                    .getProperty(portPropertyKey)
                    .shouldBe("42")
            }
        }
    }

    private fun createPropertiesFile(parentDir: File? = null, callback: (File) -> Unit): File =
        APPLICATION_PROPERTIES_FILE_NAME
            .split(".")
            .let { (prefix, suffix) -> File.createTempFile(prefix, suffix, parentDir) }
            .apply(callback)
            .apply { renameTo(File(parent.plus("/$APPLICATION_PROPERTIES_FILE_NAME"))) }
}
