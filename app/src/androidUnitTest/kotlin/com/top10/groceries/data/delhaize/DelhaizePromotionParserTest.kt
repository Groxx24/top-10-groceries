package com.top10.groceries.data.delhaize

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DelhaizePromotionParserTest {

    private val parser = DelhaizePromotionParser()

    private fun promotion(
        message: String,
        quantity: Int,
        type: String = PERCENTAGE,
        level: String = "MEMBER",
        from: String = "01/10",
        to: String = "07/10",
    ) = PromotionDto(
        code = "BE1",
        simplePromotionMessage = message,
        promotionType = type,
        qualifyingCount = quantity,
        redemptionLevel = level,
        fromDate = from,
        toDate = to,
    )

    private fun assertDeal(expectedDiscount: Double, expectedQuantity: Int, promotion: PromotionDto, price: Double = 4.0) {
        val deal = parser.parse(promotion, price)!!
        assertEquals(expectedDiscount, deal.discount, 0.001)
        assertEquals(expectedQuantity, deal.requiredQuantity)
    }

    @Test
    fun `one plus one free is half off two`() {
        assertDeal(0.5, 2, promotion("1+1_gratis", 2))
    }

    @Test
    fun `two plus one free is a third off three`() {
        assertDeal(1.0 / 3, 3, promotion("2+1 Gratis", 3))
    }

    @Test
    fun `two plus three free is sixty percent off five`() {
        assertDeal(0.6, 5, promotion("2+3 gratis", 5))
    }

    @Test
    fun `second at half price is a quarter off two in every language`() {
        assertDeal(0.25, 2, promotion("2ème à -50%", 2))
        assertDeal(0.25, 2, promotion("2de tegen -50%", 2))
        assertDeal(0.25, 2, promotion("2nd at -50%", 2))
    }

    @Test
    fun `plain percentage applies to a single item`() {
        assertDeal(0.25, 1, promotion("-25%", 1))
    }

    @Test
    fun `percentage for several applies to each of them`() {
        assertDeal(0.6666, 3, promotion("-66.66%_pour 3", 3))
    }

    @Test
    fun `tiered percentage takes the deepest step`() {
        assertDeal(0.25, 3, promotion("2 = -20% |_3 = -25%", 3))
    }

    @Test
    fun `fixed total is compared with the normal price`() {
        // 3 x 2.25 = 6.75 normally, 5 with the deal.
        assertDeal(1 - 5 / 6.75, 3, promotion("3 produits pour €5", 3, type = FIXED_TOTAL), price = 2.25)
        assertDeal(1 - 5.5 / 6.75, 3, promotion("3 produits pour €5.5", 3, type = FIXED_TOTAL), price = 2.25)
    }

    @Test
    fun `fixed total above the normal price is no deal`() {
        assertNull(parser.parse(promotion("3 produits pour €5", 3, type = FIXED_TOTAL), 1.5))
    }

    @Test
    fun `euros off is a share of what the items cost`() {
        assertDeal(0.2, 1, promotion("- €2 à l' achat de 1 produit", 1, type = EUROS_OFF), price = 10.0)
        assertDeal(0.25, 2, promotion("- €3 à l' achat de 2 produits", 2, type = EUROS_OFF), price = 6.0)
    }

    @Test
    fun `free delivery is not a deal`() {
        assertNull(parser.parse(promotion("2produits = livraison gratuite", 2, type = DELIVERY), 4.0))
    }

    @Test
    fun `year-long promotion is not an offer of the week`() {
        assertNull(parser.parse(promotion("-25%", 1, from = "01/01", to = "31/12"), 4.0))
    }

    @Test
    fun `promotion running over new year still counts`() {
        assertDeal(0.25, 1, promotion("-25%", 1, from = "24/12/26", to = "06/01/27"))
    }

    @Test
    fun `label is cleaned and the end date drops its year`() {
        val deal = parser.parse(promotion("1+1_gratis", 2, to = "07/10/26"), 4.0)!!
        assertEquals("1+1 gratis", deal.label)
        assertEquals("07/10", deal.validUntil)
    }

    @Test
    fun `member promotions need the loyalty card`() {
        assertTrue(parser.parse(promotion("-25%", 1, level = "MEMBER"), 4.0)!!.needsLoyaltyCard)
        assertFalse(parser.parse(promotion("-25%", 1, level = "MASS"), 4.0)!!.needsLoyaltyCard)
    }

    @Test
    fun `unreadable label is skipped`() {
        assertNull(parser.parse(promotion("Nouveau", 1), 4.0))
    }

    private companion object {
        const val PERCENTAGE = "Buy X Get Percentage Off All Products"
        const val FIXED_TOTAL = "Grocery Multi-buy"
        const val EUROS_OFF = "Discount X Euros For Y Articles"
        const val DELIVERY = "Buy X Number of Product(s) get a Change of Delivery Mode"
    }
}
