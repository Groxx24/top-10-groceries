package com.top10.groceries.data.hardcoded

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HardcodedCatalogTest {

    private val catalog = HardcodedCatalog()

    @Test
    fun `every store has a top 10 ranked 1 to 10`() = runTest {
        for (store in catalog.stores()) {
            val top = catalog.topOffers(store.id)
            assertEquals(store, top.store)
            assertEquals((1..10).toList(), top.offers.map { it.rank })
            assertEquals(10, top.offers.distinctBy { it.productId }.size)
        }
    }

    @Test
    fun `an unknown store id fails`() = runTest {
        val result = runCatching { catalog.topOffers("nope") }
        assertTrue(result.isFailure)
    }
}
