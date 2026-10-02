package com.top10.groceries.domain.ranking

import com.top10.groceries.domain.model.ProductCategory
import com.top10.groceries.domain.model.ProductCategory.ALCOHOL
import com.top10.groceries.domain.model.ProductCategory.MEAT_FISH
import com.top10.groceries.domain.model.ProductCategory.PANTRY
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TopOffersSelectorTest {

    private val policy = RankingPolicy()
    private val selector = TopOffersSelector(OfferScorer(policy), policy)

    @Test
    fun `list is ordered best first and numbered from one`() {
        val top = selector.select(
            listOf(
                offer(ALCOHOL, discount = 0.5, code = "wine"),
                offer(MEAT_FISH, discount = 0.5, code = "steak"),
                offer(PANTRY, discount = 0.5, code = "pasta"),
            ),
        )
        assertEquals(listOf("steak", "pasta", "wine"), top.map { it.offer.productCode })
        assertEquals(listOf(1, 2, 3), top.map { it.rank })
    }

    @Test
    fun `list holds at most ten`() {
        val offers = ProductCategory.entries.flatMap { category ->
            (1..3).map { offer(category, discount = 0.5, code = "$category-$it") }
        }
        assertEquals(10, selector.select(offers).size)
    }

    @Test
    fun `offers with too small a discount are left out`() {
        val top = selector.select(listOf(offer(MEAT_FISH, discount = 0.10), offer(PANTRY, discount = 0.5, code = "pasta")))
        assertEquals(listOf("pasta"), top.map { it.offer.productCode })
    }

    @Test
    fun `one promotion on many products takes one place, shown by its best product`() {
        val top = selector.select(
            listOf(
                offer(MEAT_FISH, discount = 0.5, price = 2.0, code = "ham", promotionId = "charcuterie"),
                offer(MEAT_FISH, discount = 0.5, price = 6.0, code = "roast", promotionId = "charcuterie"),
                offer(MEAT_FISH, discount = 0.5, price = 3.0, code = "salami", promotionId = "charcuterie"),
            ),
        )
        assertEquals(1, top.size)
        assertEquals("roast", top.single().offer.productCode)
        assertEquals(2, top.single().sameDealCount)
    }

    @Test
    fun `no category takes more than three places`() {
        val meat = (1..6).map { offer(MEAT_FISH, discount = 0.5, code = "meat-$it") }
        val pantry = (1..2).map { offer(PANTRY, discount = 0.3, code = "pantry-$it") }
        val top = selector.select(meat + pantry)
        assertEquals(3, top.count { it.offer.category == MEAT_FISH })
        assertEquals(2, top.count { it.offer.category == PANTRY })
        assertTrue(top.take(3).all { it.offer.category == MEAT_FISH })
    }

    @Test
    fun `equal scores come out in the same order every time`() {
        val offers = listOf("c", "a", "b").map { offer(PANTRY, discount = 0.5, code = it) }
        assertEquals(listOf("a", "b", "c"), selector.select(offers).map { it.offer.productCode })
        assertEquals(listOf("a", "b", "c"), selector.select(offers.reversed()).map { it.offer.productCode })
    }
}
