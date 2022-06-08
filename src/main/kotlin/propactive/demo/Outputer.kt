package propactive.demo

import propactive.demo.Properties.primePropertyKey
import propactive.demo.Properties.portPropertyKey
import propactive.demo.Properties.prodOnlyStringPropertyKey
import propactive.demo.Properties.timoutInMsPropertyKey
import propactive.demo.Properties.urlPropertyKey
import propactive.demo.Properties.uuidPropertyKey
import java.net.URL
import java.util.UUID

class Outputer(
    private val environment: String,
    private val stringPropertyValue: String,
    private val portPropertyValue: Int,
    private val urlPropertyValue: URL,
    private val timoutInMsPropertyValue: Int,
    private val uuidPropertyValue: UUID,
    private val customPropertyValue: Int,
) {
    fun displayCollectedProperties() = """
        Current ($environment) Environment Application Properties:
          - $prodOnlyStringPropertyKey = $stringPropertyValue
          - $portPropertyKey = $portPropertyValue
          - $urlPropertyKey = $urlPropertyValue
          - $timoutInMsPropertyKey = $timoutInMsPropertyValue
          - $uuidPropertyKey = $uuidPropertyValue
          - $primePropertyKey = $customPropertyValue
        """.trimIndent()
}