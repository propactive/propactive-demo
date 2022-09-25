package io.github.propactive.demo

import io.github.propactive.demo.Properties.portPropertyKey
import io.github.propactive.demo.Properties.primePropertyKey
import io.github.propactive.demo.Properties.prodOnlyStringPropertyKey
import io.github.propactive.demo.Properties.timoutInMsPropertyKey
import io.github.propactive.demo.Properties.urlPropertyKey
import io.github.propactive.demo.Properties.uuidPropertyKey
import java.net.URL
import java.util.*

object Outputer {
    fun displayCollectedProperties(
        environment: String,
        stringPropertyValue: String,
        portPropertyValue: Int,
        urlPropertyValue: URL,
        timoutInMsPropertyValue: Int,
        uuidPropertyValue: UUID,
        customPropertyValue: Int,
    ) = StringBuilder()
        .apply {
            takeIf { stringPropertyValue.isBlank().not() }?.append(
                """
                |# A String property for "prod" only environment (i.e. only written to "prod" properties file)
                |$prodOnlyStringPropertyKey=$stringPropertyValue
                |
                """.trimMargin()
            )
        }
        .append(
            """
            |# A URL property that was validate on runtime to be a valid URL without extra tests
            |$urlPropertyKey=$urlPropertyValue
            |# A Timeout integer property that was validate to be of correct type without extra tests
            |$timoutInMsPropertyKey=$timoutInMsPropertyValue
            |# A UUID property that was validate on runtime to be a valid UUID format without extra tests
            |$uuidPropertyKey=$uuidPropertyValue
            |# A port number property that was validated using the custom property type feature
            |#   See: io.github.propactive.demo.type.PORT_NUMBER
            |$portPropertyKey=$portPropertyValue
            |# A prime number property that was validated using the custom property type feature
            |#   See: io.github.propactive.demo.type.PRIME
            |$primePropertyKey=$customPropertyValue
            """.trimMargin()
        )
        .toString()
        .let { annotatedYamlApplicationPropertiesFile ->
            """
            |
            |### The dynamically generated properties file:
            |
            |```yaml
            |# application.properties ($environment)
            |$annotatedYamlApplicationPropertiesFile
            |```
            |
            |The dynamic properties class can be found here: [io.github.propactive.demo.Properties](https://github.com/propactive/propactive-demo/blob/master/src/main/kotlin/io/github/propactive/demo/Properties.kt)
            |The generated properties file for this environment can be found within the [build's artifacts](#artifacts). 
            |
            |#### What are you looking at?
            |
            |This is a demo for the framework [Propactive](https://github.com/propactive/propactive#readme). It demonstrates the ability to:
            |  - Create properties file using the JVM in a flexible manner.
            |  - Control which properties files to generate.
            |  - Plus validate property files on runtime with custom types.
            |
            |The benefit of such approach is evident for enterprise microservices
            |that rely on multiple properties and are deployed to different environments.
            |
            |For further details, see: [Propactive Framework's README](https://github.com/propactive/propactive#readme)
            |
            |------------------------------------------------------------------------------------------------
            """.trimMargin()
        }
}