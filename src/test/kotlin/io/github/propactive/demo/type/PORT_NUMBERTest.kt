package io.github.propactive.demo.type

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@Suppress("ClassName")
internal class PORT_NUMBERTest {
    @CsvSource("1", "1024", "1025", "49151", "49152", "65535")
    @ParameterizedTest
    fun shouldConsiderRegisteredPortValuesAsValid(number: Int) {
        assertTrue(PORT_NUMBER.validate(number))
    }

    @CsvSource("-1", "-1024", "65536", "808080", "1000000")
    @ParameterizedTest
    fun shouldConsiderNonRegisteredPortValuesAsInvalid(number: Int) {
        assertFalse(PORT_NUMBER.validate(number))
    }
}