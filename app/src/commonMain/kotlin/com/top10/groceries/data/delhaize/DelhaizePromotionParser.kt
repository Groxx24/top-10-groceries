package com.top10.groceries.data.delhaize

import com.top10.groceries.domain.model.Deal

/**
 * Reduces a Delhaize promotion to a [Deal]. The type and the required quantity come as data;
 * the amounts only exist inside the label, so they are read out of it. Only digits and symbols
 * are matched, never words, because the label is in whichever language was requested.
 */
class DelhaizePromotionParser {

    /**
     * The deal [promotion] gives on a product costing [price], or null when it is not a price
     * reduction (free delivery), runs too long to be an offer of the week, or cannot be read.
     */
    fun parse(promotion: PromotionDto, price: Double): Deal? {
        val id = promotion.code ?: return null
        val label = promotion.simplePromotionMessage?.replace('_', ' ')?.trim() ?: return null
        if (price <= 0.0) return null
        if (isStanding(promotion.fromDate, promotion.toDate)) return null

        val quantity = promotion.qualifyingCount?.takeIf { it >= 1 } ?: 1
        val (discount, requiredQuantity) = when (promotion.promotionType) {
            TYPE_DELIVERY -> return null
            TYPE_FIXED_TOTAL -> fixedTotal(label, price, quantity)
            TYPE_EUROS_OFF -> eurosOff(label, price, quantity)
            else -> percentage(label, quantity)
        } ?: return null
        if (discount <= 0.0 || discount >= 1.0) return null

        return Deal(
            promotionId = id,
            label = label,
            discount = discount,
            requiredQuantity = requiredQuantity,
            needsLoyaltyCard = promotion.redemptionLevel == LEVEL_MEMBER,
            validUntil = promotion.toDate?.take(DAY_MONTH_LENGTH),
        )
    }

    /** "3 produits pour €5": the last number is what [quantity] items cost together. */
    private fun fixedTotal(label: String, price: Double, quantity: Int): Pair<Double, Int>? {
        val total = numbers(label).lastOrNull() ?: return null
        return 1.0 - total / (price * quantity) to quantity
    }

    /** "- €2 à l'achat de 1 produit": the first number is taken off [quantity] items together. */
    private fun eurosOff(label: String, price: Double, quantity: Int): Pair<Double, Int>? {
        val off = numbers(label).firstOrNull() ?: return null
        return off / (price * quantity) to quantity
    }

    private fun percentage(label: String, quantity: Int): Pair<Double, Int>? {
        // "2+1 gratis": buy 2, get 1 more free.
        FREE_ITEMS.find(label)?.let { match ->
            val paid = match.groupValues[1].toInt()
            val free = match.groupValues[2].toInt()
            if (paid < 1 || free < 1) return null
            return free.toDouble() / (paid + free) to paid + free
        }

        // "2 = -20% | 3 = -25%": the deepest step is the one worth comparing.
        TIER.findAll(label)
            .map { it.groupValues[2].toAmount() / 100 to it.groupValues[1].toInt() }
            .maxByOrNull { it.first }
            ?.let { return it }

        val percent = PERCENT.find(label)?.groupValues?.get(1)?.toAmount() ?: return null
        return if (label.startsWith("-")) {
            // "-25%" or "-25% pour 3": off every item, once [quantity] are bought.
            percent / 100 to quantity
        } else {
            // "2ème à -50%": off the last item only, so spread over all of them.
            percent / 100 / quantity to quantity
        }
    }

    /**
     * True for promotions that run for months, such as the year-long ones Delhaize lists next to
     * the weekly folder. The dates come without a reliable year, so a period that seems to end
     * before it starts is read as running into the next year.
     */
    private fun isStanding(from: String?, to: String?): Boolean {
        val start = dayOfYear(from) ?: return false
        val end = dayOfYear(to) ?: return false
        val days = if (end >= start) end - start else end - start + DAYS_IN_YEAR
        return days > MAX_OFFER_DAYS
    }

    private fun dayOfYear(date: String?): Int? {
        val parts = date?.split('/') ?: return null
        val day = parts.getOrNull(0)?.toIntOrNull() ?: return null
        val month = parts.getOrNull(1)?.toIntOrNull()?.takeIf { it in 1..12 } ?: return null
        return DAYS_BEFORE_MONTH[month - 1] + day
    }

    private fun numbers(text: String): List<Double> =
        NUMBER.findAll(text).map { it.value.toAmount() }.toList()

    private fun String.toAmount(): Double = replace(',', '.').toDouble()

    private companion object {
        const val TYPE_DELIVERY = "Buy X Number of Product(s) get a Change of Delivery Mode"
        const val TYPE_FIXED_TOTAL = "Grocery Multi-buy"
        const val TYPE_EUROS_OFF = "Discount X Euros For Y Articles"
        const val LEVEL_MEMBER = "MEMBER"

        const val DAY_MONTH_LENGTH = 5
        const val DAYS_IN_YEAR = 365
        const val MAX_OFFER_DAYS = 42
        val DAYS_BEFORE_MONTH = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)

        val NUMBER = Regex("""\d+(?:[.,]\d+)?""")
        val FREE_ITEMS = Regex("""(\d+)\s*\+\s*(\d+)""")
        val TIER = Regex("""(\d+)\s*=\s*-\s*(\d+(?:[.,]\d+)?)\s*%""")
        val PERCENT = Regex("""-\s*(\d+(?:[.,]\d+)?)\s*%""")
    }
}
