package propactive.demo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import propactive.demo.Properties.primePropertyKey
import propactive.demo.Properties.portPropertyKey
import propactive.demo.Properties.prodOnlyStringPropertyKey
import propactive.demo.Properties.timoutInMsPropertyKey
import propactive.demo.Properties.urlPropertyKey
import propactive.demo.Properties.uuidPropertyKey
import propactive.demo.shared.PropertiesTestCase

class OutputerTest: PropertiesTestCase() {

    @Test
    fun shouldCorrectlyMapCollectedPropertiesToRspectiveKeys() {
        val collectedProperties = Outputer(
            environmentValue,
            stringPropertyValue,
            portPropertyValue,
            urlPropertyValue,
            timoutInMsPropertyValue,
            uuidPropertyValue,
            customPropertyValue,
        ).displayCollectedProperties()

        val expectedValue = """
            Current ($environmentValue) Environment Application Properties:
              - $prodOnlyStringPropertyKey = $stringPropertyValue
              - $portPropertyKey = $portPropertyValue
              - $urlPropertyKey = $urlPropertyValue
              - $timoutInMsPropertyKey = $timoutInMsPropertyValue
              - $uuidPropertyKey = $uuidPropertyValue
              - $primePropertyKey = $customPropertyValue
            """.trimIndent()

        assertEquals(collectedProperties, expectedValue)
    }
}