package com.top10.groceries.domain.ranking

import com.top10.groceries.domain.model.ProductCategory.ALCOHOL
import com.top10.groceries.domain.model.ProductCategory.BREAD
import com.top10.groceries.domain.model.ProductCategory.DRINKS
import com.top10.groceries.domain.model.ProductCategory.FRUIT_VEGETABLES
import com.top10.groceries.domain.model.ProductCategory.MEAT_FISH
import com.top10.groceries.domain.model.ProductCategory.PANTRY
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OfferScorerTest {

    private val scorer = OfferScorer()

    private fun score(offer: com.top10.groceries.domain.model.Offer) = scorer.score(offer)!!.value

    @Test
    fun `half price staple bought alone scores a hundred`() {
        assertEquals(100.0, score(offer(MEAT_FISH, discount = 0.5, price = 10.0)), 0.001)
    }

    @Test
    fun `discount at or under the floor scores nothing`() {
        assertNull(scorer.score(offer(MEAT_FISH, discount = 0.10)))
        assertNull(scorer.score(offer(MEAT_FISH, discount = 0.15)))
    }

    @Test
    fun `twenty percent on meat loses to half price on almost anything`() {
        val meatAtTwenty = score(offer(MEAT_FISH, discount = 0.20))
        assertTrue(meatAtTwenty < score(offer(PANTRY, discount = 0.5)))
        assertTrue(meatAtTwenty < score(offer(DRINKS, discount = 0.5)))
        assertTrue(meatAtTwenty < score(offer(ALCOHOL, discount = 0.5)))
    }

    @Test
    fun `same discount ranks staples above the rest`() {
        val meat = score(offer(MEAT_FISH, discount = 0.33))
        val pantry = score(offer(PANTRY, discount = 0.33))
        val alcohol = score(offer(ALCOHOL, discount = 0.33))
        assertTrue(meat > pantry && pantry > alcohol)
        assertEquals(meat, score(offer(BREAD, discount = 0.33)), 0.001)
        assertEquals(meat, score(offer(FRUIT_VEGETABLES, discount = 0.33)), 0.001)
    }

    @Test
    fun `a decent discount on meat beats half price on alcohol`() {
        assertTrue(score(offer(MEAT_FISH, discount = 0.25, quantity = 2)) > score(offer(ALCOHOL, discount = 0.5, quantity = 2)))
    }

    @Test
    fun `deeper discount always scores higher within a category`() {
        val scores = listOf(0.2, 0.25, 0.33, 0.5).map { score(offer(MEAT_FISH, discount = it, price = 10.0)) }
        assertEquals(scores.sorted(), scores)
    }

    @Test
    fun `discount beyond half price is capped`() {
        assertEquals(
            score(offer(PANTRY, discount = 0.6, price = 20.0)),
            score(offer(PANTRY, discount = 0.9, price = 20.0)),
            0.001,
        )
    }

    @Test
    fun `having to buy more items costs points`() {
        // Same discount and same euros saved, so only the quantity differs.
        val one = score(offer(PANTRY, discount = 0.5, quantity = 1, price = 12.0))
        val two = score(offer(PANTRY, discount = 0.5, quantity = 2, price = 6.0))
        val six = score(offer(PANTRY, discount = 0.5, quantity = 6, price = 2.0))
        assertTrue(one > two && two > six)
    }

    @Test
    fun `saving more euros earns a little, up to the target`() {
        val small = score(offer(PANTRY, discount = 0.5, price = 1.0))
        val large = score(offer(PANTRY, discount = 0.5, price = 10.0))
        val larger = score(offer(PANTRY, discount = 0.5, price = 40.0))
        assertTrue(large > small)
        assertTrue(large / small < 1.25)
        assertEquals(large, larger, 0.001)
    }
}
