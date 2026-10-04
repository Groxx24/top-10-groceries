package com.top10.deals.domain.usecase

import com.top10.deals.domain.model.LocalizedText
import com.top10.deals.domain.model.Offer
import com.top10.deals.domain.model.ProductCategory
import com.top10.deals.domain.model.Store
import com.top10.deals.domain.model.TopOffers
import com.top10.deals.domain.model.WeeklyDeal
import com.top10.deals.domain.repository.PublishedTopLists
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class DeleteEndedTopListsUseCaseTest {

    private val lists = FakePublishedTopLists()
    // Sunday 4 October 2026.
    private val deleteEnded = DeleteEndedTopListsUseCase(lists) { LocalDate(2026, 10, 4) }

    @Test
    fun `only the lists whose first deal ended before today are deleted`() = runTest {
        lists.add("aldi", offer(1, until = "03/10"), offer(2, until = "10/10"))
        lists.add("lidl", offer(1, until = "04/10"))
        lists.add("delhaize", offer(1, until = "07/10"))
        lists.add("spar", offer(1, until = null))

        val deleted = deleteEnded()

        assertEquals(listOf("aldi"), deleted.map { it.id })
        assertEquals(setOf("lidl", "delhaize", "spar"), lists.lists.keys)
    }

    @Test
    fun `nothing is deleted when no list has ended`() = runTest {
        lists.add("delhaize", offer(1, until = "07/10"))

        assertEquals(emptyList<Store>(), deleteEnded())
        assertEquals(setOf("delhaize"), lists.lists.keys)
    }

    private fun offer(rank: Int, until: String?) = Offer(
        rank = rank,
        deal = WeeklyDeal(
            id = "deal-$rank", name = LocalizedText.same("Bananas"), brand = null, packageSize = null, label = null,
            price = 1.49, category = ProductCategory.FRUIT, validUntil = until,
        ),
    )

    private class FakePublishedTopLists : PublishedTopLists {
        val lists = mutableMapOf<String, TopOffers>()

        fun add(storeId: String, vararg offers: Offer) {
            lists[storeId] = TopOffers(Store(storeId, storeId), offers.toList())
        }

        override suspend fun all(): List<TopOffers> = lists.values.toList()

        override suspend fun delete(storeId: String) {
            lists.remove(storeId)
        }
    }
}
