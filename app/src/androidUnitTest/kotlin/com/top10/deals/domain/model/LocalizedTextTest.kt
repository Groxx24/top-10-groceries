package com.top10.deals.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class LocalizedTextTest {

    private val text = LocalizedText(en = "Jumbo mussels", fr = "Moules jumbo", nl = "Jumbo mosselen")

    @Test
    fun `French and Dutch phones get their language, any other gets English`() {
        assertEquals("Moules jumbo", text.inLanguage("fr"))
        assertEquals("Jumbo mosselen", text.inLanguage("nl"))
        assertEquals("Jumbo mussels", text.inLanguage("en"))
        assertEquals("Jumbo mussels", text.inLanguage("de"))
    }
}
