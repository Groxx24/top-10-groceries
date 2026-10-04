package com.top10.deals.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ProductCategoryTest {

    @Test
    fun `a stored name gives its category back`() {
        for (category in ProductCategory.entries) {
            assertEquals(category, ProductCategory.fromName(category.name))
        }
    }

    @Test
    fun `a missing or unknown name is OTHER`() {
        assertEquals(ProductCategory.OTHER, ProductCategory.fromName(null))
        assertEquals(ProductCategory.OTHER, ProductCategory.fromName("SPACESHIPS"))
    }
}
