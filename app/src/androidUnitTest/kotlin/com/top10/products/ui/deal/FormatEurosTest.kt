package com.top10.products.ui.deal

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatEurosTest {

    @Test
    fun `amounts round to cents with the language's decimal separator`() {
        assertEquals("5.85", formatEuros(5.853))
        assertEquals("3,09", formatEuros(3.09, ","))
        assertEquals("8,00", formatEuros(8.0, ","))
        assertEquals("0,05", formatEuros(0.049, ","))
    }
}
