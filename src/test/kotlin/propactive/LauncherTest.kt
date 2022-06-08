package propactive

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.extensions.system.OverrideMode
import io.kotest.extensions.system.OverrideMode.SetOrOverride
import io.kotest.extensions.system.withEnvironment
import io.kotest.extensions.system.withSystemProperty
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import propactive.Launcher.APPLICATION_PROPERTIES_CONFIG_DIR_PATH_KEY
import propactive.Launcher.APPLICATION_PROPERTIES_FILE_NAME
import propactive.Launcher.ENVIRONMENT_KEY
import propactive.Launcher.loadFrom
import propactive.demo.Properties.portPropertyKey
import propactive.demo.Properties.primePropertyKey
import propactive.demo.Properties.prodOnlyStringPropertyKey
import propactive.demo.Properties.timoutInMsPropertyKey
import propactive.demo.Properties.urlPropertyKey
import propactive.demo.Properties.uuidPropertyKey
import propactive.demo.shared.PropertiesTestCase
import java.io.File
import java.util.Properties

class LauncherTest: PropertiesTestCase() {
    @Nested
    @TestInstance(PER_CLASS)
    inner class Main {
        @Test
        fun shouldBeAbleToLaunchAppWhenExpectedPropertiesAreProvided() {
            withEnvironment(ENVIRONMENT_KEY, environmentValue) {
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
            withSystemProperty(APPLICATION_PROPERTIES_CONFIG_DIR_PATH_KEY, if (value == "blank") "" else null) {
                shouldThrow<IllegalStateException> {
                    Properties().loadFrom(APPLICATION_PROPERTIES_CONFIG_DIR_PATH_KEY)
                }.message shouldBe "Could not load properties from -D$APPLICATION_PROPERTIES_CONFIG_DIR_PATH_KEY as it's not set or has blank value"
            }
        }

        @Test
        fun shouldBeAbleToLoadPropertiesFromValidPropertiesFile() {
            val propertiesFile = createPropertiesFile { it.writeText("$portPropertyKey=42") }

            withSystemProperty(APPLICATION_PROPERTIES_CONFIG_DIR_PATH_KEY, propertiesFile.canonicalFile.parent) {
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