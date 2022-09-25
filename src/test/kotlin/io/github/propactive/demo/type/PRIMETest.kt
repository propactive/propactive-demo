package io.github.propactive.demo.type

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class PRIMETest {
    @CsvSource("3", "5", "7", "11", "13",)
    @ParameterizedTest
    fun shouldConsiderPrimeValuesAsValid(number: Int) {
        assertTrue(PRIME.validate(number))
    }

    @CsvSource("4", "6", "8", "12", "14",)
    @ParameterizedTest
    fun shouldConsiderNonPrimeValuesAsInvalid(number: Int) {
        assertFalse(PRIME.validate(number))
    }
}