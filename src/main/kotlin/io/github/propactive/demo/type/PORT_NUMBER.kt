package io.github.propactive.demo.type

import io.github.propactive.type.Type

/**
 * A custom type that checks if a number is within the registered range of port numbers. (i.e. 1..65535)
 * See [RFC 1700](http://www.ietf.org/rfc/rfc1700.txt?number=1700) for further details.
 */
@Suppress("ClassName")
object PORT_NUMBER : Type {
    override fun validate(value: Any) = value
        .runCatching { toString().toInt() }
        .getOrDefault(-1)
        .let { number -> number in (1..65535) }
}
