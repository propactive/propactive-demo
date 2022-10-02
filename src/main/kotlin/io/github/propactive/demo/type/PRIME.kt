package io.github.propactive.demo.type

import io.github.propactive.type.INTEGER
import io.github.propactive.type.Type
import io.github.propactive.type.Type.Companion.INVALID
import io.github.propactive.type.Type.Companion.VALID

/**
 * A custom type that checks if a number is Prime.
 * This also make use of exiting natively supported types for initial validation.
 */
object PRIME : Type {
    override fun validate(value: Any) = value
        .takeIf(INTEGER::validate)
        ?.toString()
        ?.toInt()
        ?.runCatching { for (i in 2..this / 2) check(this % i != 0); VALID }
        ?.getOrNull()
        ?: INVALID
}
