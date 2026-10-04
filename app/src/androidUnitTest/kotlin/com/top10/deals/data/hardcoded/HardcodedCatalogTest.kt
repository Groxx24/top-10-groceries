package com.top10.deals.data.hardcoded

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class HardcodedCatalogTest {

    @Test
    fun `there are five stores with distinct ids`() = runTest {
        val stores = HardcodedCatalog().stores()
        assertEquals(listOf("delhaize", "aldi", "lidl", "intermarche", "spar"), stores.map { it.id })
    }
}
