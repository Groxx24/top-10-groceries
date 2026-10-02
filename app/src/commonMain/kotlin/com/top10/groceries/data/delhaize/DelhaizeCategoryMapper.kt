package com.top10.groceries.data.delhaize

import com.top10.groceries.domain.model.ProductCategory

/**
 * Maps Delhaize's category tree onto [ProductCategory]. The top level comes as a code that is
 * the same in every language. A few top levels mix things the ranking treats differently
 * (falafel under meat, coconut water under fruit, cake under bakery), and those are told apart
 * by the second level, which only exists as a translated slug in the product's URL.
 */
class DelhaizeCategoryMapper {

    fun map(topLevelCode: String?, productUrl: String?): ProductCategory {
        val secondLevel = secondLevel(productUrl)
        return when (topLevelCode?.uppercase()) {
            "V2MEA" -> if (secondLevel.containsAny(VEGETARIAN)) ProductCategory.VEGETARIAN else ProductCategory.MEAT_FISH
            "V2FRU" -> if (secondLevel.containsAny(JUICE)) ProductCategory.DRINKS else ProductCategory.FRUIT_VEGETABLES
            "V2BAK" -> if (secondLevel.containsAny(PASTRY)) ProductCategory.SWEETS_PASTRY else ProductCategory.BREAD
            "V2DAI" -> ProductCategory.DAIRY_EGGS
            "V2CON" -> ProductCategory.READY_MEALS
            "V2FRO" -> ProductCategory.FROZEN
            "V2SAL" -> ProductCategory.PANTRY
            "V2CAN" -> ProductCategory.CANNED
            "V2SPE" -> ProductCategory.SPECIAL_DIET
            "V2DRI" -> ProductCategory.DRINKS
            "V2SWE" -> ProductCategory.SWEETS_PASTRY
            "V2HYG", "V2SPO" -> ProductCategory.HYGIENE
            "V2CLE" -> ProductCategory.HOUSEHOLD
            "V2ALC", "V2WIN" -> ProductCategory.ALCOHOL
            "V2NON" -> ProductCategory.HOME
            "V2BAB" -> ProductCategory.BABY
            "V2PET" -> ProductCategory.PET
            else -> ProductCategory.OTHER
        }
    }

    /** ".../shop/<top level>/<second level>/..." gives the second level, lower-cased. */
    private fun secondLevel(productUrl: String?): String {
        val segments = productUrl?.split('/').orEmpty()
        val shop = segments.indexOf("shop")
        return if (shop < 0) "" else segments.getOrNull(shop + 2).orEmpty().lowercase()
    }

    private fun String.containsAny(keywords: List<String>): Boolean = keywords.any { it in this }

    private companion object {
        // Slugs seen in French, Dutch and English.
        val VEGETARIAN = listOf("veget", "vegan")
        val JUICE = listOf("jus", "sap", "juice", "smoothie")
        val PASTRY = listOf("patisserie", "pastry", "gebak", "banket")
    }
}
