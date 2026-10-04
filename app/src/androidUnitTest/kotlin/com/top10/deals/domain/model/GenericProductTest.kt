package com.top10.deals.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GenericProductTest {

    @Test
    fun `a stored name gives its product back`() {
        for (product in GenericProduct.entries) {
            assertEquals(product, GenericProduct.fromName(product.name))
        }
    }

    @Test
    fun `a missing or unknown name is no product`() {
        assertNull(GenericProduct.fromName(null))
        assertNull(GenericProduct.fromName("SPACESHIPS"))
    }
}
