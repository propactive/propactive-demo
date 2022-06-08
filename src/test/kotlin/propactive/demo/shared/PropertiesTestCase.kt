package propactive.demo.shared

import propactive.Launcher
import java.io.File
import java.net.URL
import java.util.*

abstract class PropertiesTestCase {
    protected val environmentValue = "dev"
    protected val stringPropertyValue = "string"
    protected val portPropertyValue = 42
    protected val urlPropertyValue = URL("https://www.propactive.com")
    protected val timoutInMsPropertyValue = 3000
    protected val uuidPropertyValue: UUID = UUID.randomUUID()
    protected val customPropertyValue = 7
}