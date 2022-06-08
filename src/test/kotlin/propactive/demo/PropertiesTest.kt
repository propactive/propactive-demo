package propactive.demo

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import propactive.demo.Properties.prodOnlyStringPropertyKey
import propactive.demo.Properties.timoutInMsPropertyKey
import propactive.demo.Properties.urlPropertyKey
import propactive.environment.EnvironmentFactory
import propactive.property.PropertyModel

class PropertiesTest {
    @Test
    fun shouldHaveTimeoutLargerThan250ms() {
        findAllMatchingPropertiesFor(timoutInMsPropertyKey)
            .forEach {
                assertTrue(
                    it.value.toInt() > 250,
                    "Expected: $timoutInMsPropertyKey for environment: ${it.environment} to have a value larger than 250ms but was: ${it.value}"
                )
            }
    }

    @Test
    fun shouldUseASecureProdUrlProtocol() {
        findAllMatchingPropertiesFor(urlPropertyKey)
            .first { it.environment == "prod" }
            .value.apply { assertTrue(contains(Regex("^https"))) }
    }

    @Test
    fun shouldNotGenerateKeyValuesForNonProdEnvForProdOnlyStringPropertyKey() {
        findAllMatchingPropertiesFor(prodOnlyStringPropertyKey)
            .firstOrNull { it.environment != "prod" }
            ?.apply { fail("Expected to not find $prodOnlyStringPropertyKey key in non prod application property files") }
    }

    private fun findAllMatchingPropertiesFor(propertyKey: String): List<PropertyModel> = EnvironmentFactory
        .create(Properties::class)
        .mapNotNull { env -> env.properties.firstOrNull { it.name == propertyKey } }
}