package com.top10.products.data.hardcoded

import com.top10.products.domain.model.WeeklyDeal
import com.top10.products.domain.repository.WeeklyDealsRepository

/**
 * This week's deals per store, typed in by hand until they are scraped. Delhaize's are copied from
 * its own folder. The other stores' come from Belgian folder sites as they listed them on
 * 3 October 2026, which showed only a handful per store and are not checked against the folders;
 * the rest are made up (see [madeUp]) to make 20.
 */
class HardcodedWeeklyDeals : WeeklyDealsRepository {

    override suspend fun weeklyDeals(storeId: String): List<WeeklyDeal> =
        DEALS[storeId] ?: error("No weekly deals for $storeId")

    private companion object {
        // Declared before DEALS, which reads it while the companion is initialised.
        /** Made-up staples that pad a store's list to 20 where the folder sites showed fewer. */
        val MADE_UP = listOf(
            deal("Chicken breast fillets", null, "600 g", "1+1 gratis", 8.49),
            deal("Atlantic salmon fillets", null, "250 g", null, 4.79, was = 7.99),
            deal("Minced beef and pork", null, "1 kg", null, 6.50, was = 9.29),
            deal("Free-range eggs", null, "12 pieces", "2+1 gratis", 4.19),
            deal("Vine tomatoes", null, "500 g", null, 1.81, was = 2.79),
            deal("Jonagold apples", null, "1.5 kg", "1+1 gratis", 3.99),
            deal("Bananas", null, "1 kg", null, 1.49, was = 1.99),
            deal("Semi-skimmed milk", null, "6 x 1 L", null, 5.50, was = 6.89),
            deal("Butter", null, "250 g", "2de aan halve prijs", 2.99),
            deal("Gouda cheese slices", null, "400 g", null, 3.49, was = 4.99),
            deal("Greek yoghurt", null, "4 x 150 g", null, 3.01, was = 4.49),
            deal("Sourdough loaf", null, "800 g", "2de aan halve prijs", 3.59),
            deal("Penne rigate", null, "1 kg", null, 1.73, was = 2.89),
            deal("Lasagne bolognese", null, "1 kg", null, 5.24, was = 7.49),
            deal("Ground coffee", null, "500 g", null, 5.99, was = 7.99),
            deal("Orange juice", null, "1 L", "2+1 gratis", 2.49),
            deal("Toilet paper", null, "12 rolls", null, 4.89, was = 6.99),
            deal("Frozen pizza margherita", null, "3 x 350 g", null, 5.99, was = 7.99),
        )

        // The label is the deal as the source printed it, or null when it gave only prices.
        val DEALS: Map<String, List<WeeklyDeal>> = mapOf(
            // From Delhaize's own folder for week 40 (01/10 to 07/10/2026), names and deals as
            // printed. Every Delhaize promo needs a SuperPlus card, registered or not. Prices, and
            // the original prices next to them, are for the quantity in the folder's example,
            // hence the unit; the folder prints no original price for some.
            "delhaize" to deals(
                "delhaize",
                listOf(
                    deal("Chicons", null, "650 g", "1+1 gratis", null, card = true, until = "07/10"),
                    deal("Produit veggie ou veggie bio", "Delhaize", "150 g à 500 g", "1+1 gratis", 3.09, "/2", was = 6.18, card = true, until = "07/10"),
                    deal("Œufs de poules élevées en plein air", null, "6 pièces", "2e à -50%", 3.23, "/2", was = 4.30, card = true, until = "07/10"),
                    deal("Moules jumbo", null, "1 kg ou 2 kg", "2e à -50%", 11.97, "/2", was = 15.96, card = true, until = "07/10"),
                    deal("Aiguillettes de poulet", null, "Poids variable", "2e à -50%", 9.67, "/kg", was = 12.89, card = true, until = "07/10"),
                    deal("Viande de boeuf: châteaubriand, contrefilet ou rumsteak", null, "Poids variable", "-20%", 19.99, "/kg", was = 24.99, card = true, until = "07/10"),
                    deal("Dos de cabillaud", null, "Poids variable", "2e à -40%", 31.99, "/kg", was = 39.99, card = true, until = "07/10"),
                    deal("Charcuterie", "Breydel", "150 g, 300 g ou poids variable", "2e à -50%", 6.59, "/2", was = 8.78, card = true, until = "07/10"),
                    deal("Fromage", "Oudendijk", "180 g, 250 g ou poids variable", "2e à -50%", 6.74, "/2", was = 8.98, card = true, until = "07/10"),
                    deal("Beurre ou crème culinaire", "Carlsbourg", "200 g à 250 g, 200 ml ou 250 ml", "2e à -50%", 3.68, "/2", was = 4.90, card = true, until = "07/10"),
                    deal("Pâtes", "Panzani", "500 g", "2e à -50%", 3.29, "/2", was = 4.38, card = true, until = "07/10"),
                    deal("Légumes en conserve", "Bonduelle", "255 g à 800 g", "1+1 gratis", 2.49, "/2", was = 4.98, card = true, until = "07/10"),
                    deal("Pizza Holy Slice", "Iglo", "327 g à 570 g", "1+1 gratis", 5.49, "/2", was = 10.98, card = true, until = "07/10"),
                    deal("Frites spéciales ou patates rissolées", "Belviva", "600 g ou 750 g", "1+1 gratis", 4.49, "/2", was = 8.98, card = true, until = "07/10"),
                    deal("Café en grains ou moulu", "Lavazza", "250 g à 1 kg", "2e à -70%", 10.39, "/2", was = 15.98, card = true, until = "07/10"),
                    deal("Coca-Cola Original ou Zero", "Coca-Cola", "12 x 37,5 cl", "9+3 gratis", 11.70, "/promopack", card = true, until = "07/10"),
                    deal("Plus de 250 vins et bulles", null, "75 cl", "2+1 gratis", null, card = true, until = "21/10"),
                    deal("Produit pour la lessive ou détachant", "Dash", "15 à 80 doses", "1+1 gratis", 22.99, "/2", was = 45.98, card = true, until = "14/10"),
                    deal("Papier toilette Moltonel", "Lotus", "18 rouleaux", "12+6 gratis", 9.32, "/promopack", card = true, until = "14/10"),
                    deal("Langes Skin Love", "Pampers", "23 à 29 pièces", "-50%", 8.00, was = 15.99, card = true, until = "07/10"),
                    deal("Donut Worry Be Happy", null, "Pièce", "3+3 gratis", 3.75, "/6", was = 7.50, card = true, until = "07/10"),
                    deal("Yaourt pomme/cannelle", "Delhaize", "180 g", "2e à -50%", 1.43, "/2", was = 1.90, card = true, until = "07/10"),
                ),
            ),
            // Folder valid 28/09 to 03/10. ALDI prints most deals as a price only.
            "aldi" to deals(
                "aldi",
                listOf(
                    deal("Avocados", null, "3 st.", null, 2.58, was = 3.87, until = "03/10"),
                    deal("Mini chicory", null, null, "-29%", null, until = "03/10"),
                    deal("Hamburgers, family pack", null, null, null, 8.99, until = "03/10"),
                    deal("Kiwi Sungold Jumbo", "Zespri", null, null, 3.29, until = "03/10"),
                    deal("Grated Emmental", "Milsani", null, null, 2.99, until = "03/10"),
                    deal("Fish fingers", "Iglo", null, null, 6.38, until = "03/10"),
                    deal("Chicken and turkey crunchies", null, "10 pieces", null, null, until = "03/10"),
                    deal("Cola zero sugar", "Pepsi", "6 x 1.5 L", null, 8.99, until = "03/10"),
                    deal("Mineral water", "Evian", null, null, 13.39, until = "03/10"),
                    deal("Beer, cold grip", "Stella Artois", null, null, 12.13, until = "03/10"),
                    deal("Liquid laundry detergent", "Dreft", null, "1+1 gratis", 10.49, until = "03/10"),
                    deal("Crisps", "Sun Snacks", null, null, 1.99, until = "03/10"),
                    deal("Mixed snacks", "Sun Snacks", null, null, 3.00, until = "03/10"),
                    deal("Ginger beer", "River", null, null, 3.99, until = "03/10"),
                    deal("Baby wipes Sensitive", "Mamia", "80 pieces", "+20 points with the app", null, card = true, until = "03/10"),
                    deal("Intimate wash", "Lactacyd", "2 x 300 ml", "1+1 gratis", 11.99, until = "03/10"),
                    deal("Dry dog food", "Romeo", null, null, 2.99, until = "03/10"),
                    deal("Dental sticks for dogs", "Romeo", null, null, 2.99, until = "03/10"),
                    deal("Cream liqueur", "Blackstone", null, null, 8.99, until = "03/10"),
                    deal("Denture cleaning tablets", "Steradent", null, null, 5.99, until = "03/10"),
                    deal("1-2-Spray mop", "Vileda", null, null, 16.99, until = "03/10"),
                    deal("Diet tea", "Juvamine", null, null, 2.99, until = "03/10"),
                ),
            ),
            // Week 40 folder (28/09 to 03/10) and week 41 folder (05/10 to 10/10).
            "lidl" to deals(
                "lidl",
                listOf(
                    deal("Chicken breast fillet XXL", null, "1.5 kg", "-31%", 9.99, until = "03/10"),
                    deal("Energy drink", "Red Bull", "2 x 6 x 25 cl", "2de aan -50%", 7.49, until = "10/10"),
                    deal("Lemonade", "Schweppes", "2 x 6 x 50 cl", null, null, until = "10/10"),
                    deal("Energy drink", "Monster", "3 x 55.3 cl", null, 3.00, until = "03/10"),
                    deal("Laundry detergent or softener", "Dreft", "2 x 32 washes", null, 5.69, until = "03/10"),
                    deal("Liquid laundry detergent", "Ariel", "2 x 25 washes", null, 22.99, until = "10/10"),
                ) + madeUp(14, except = setOf("Chicken breast fillets")),
            ),
            // Loyalty card offers valid through October.
            "intermarche" to deals(
                "intermarche",
                listOf(
                    deal("Coffee pads", "Lavazza", null, "€4.00 off per 2, €8.00 on Thursdays", null, card = true, until = "31/10"),
                    deal("Starbucks capsules", "Nescafé Dolce Gusto", null, "€1.50 off per 2, €3.00 on Thursdays", null, card = true, until = "31/10"),
                    deal("Liquid laundry detergent", "Fleuril", null, "€4.00 off per 2", null, card = true, until = "31/10"),
                ) + madeUp(17, except = setOf("Ground coffee")),
            ),
            // Folder valid 24/09 to 07/10.
            "spar" to deals(
                "spar",
                listOf(
                    deal("Jumbo mussels", null, "1 kg", null, 7.00, until = "07/10"),
                    deal("Jonagold apples", null, "4 x 800 g", null, null, until = "07/10"),
                    deal("Extra protein milk", "Campina", "2 x 1 L", "1+1 gratis", 1.89, until = "07/10"),
                    deal("Greek-style yoghurt", "Danone", "2 x 440 g", null, 2.29, until = "07/10"),
                    deal("Yoghurt, plain, strawberry or lemon", "Danone", null, "1+1 gratis", null, until = "07/10"),
                    deal("Ricotta", "Spar", "250 g", "1+1 gratis", null, until = "07/10"),
                    deal("Pasta", "De Cecco", "250 g or 500 g", "2+2 gratis", 4.36, until = "07/10"),
                    deal("Mango", null, "1 piece", "1+1 gratis", null, until = "07/10"),
                    deal("Coca-Cola, Fanta, Sprite, Fuze Tea, Aquarius or Powerade", null, null, null, 1.50, until = "07/10"),
                    deal("Protein cereal bars, cocoa", "Nestlé Fitness", "4 x 20 g", "1+1 gratis", null, until = "07/10"),
                    deal("Ice cream", "Ben & Jerry's", null, "1+1 gratis", null, until = "07/10"),
                    deal("Toothpaste", "Sensodyne", null, "2+2 gratis", null, until = "07/10"),
                ) + madeUp(8, except = setOf("Jonagold apples", "Greek yoghurt", "Penne rigate")),
            ),
        )

        fun madeUp(count: Int, except: Set<String> = emptySet()): List<WeeklyDeal> =
            MADE_UP.filter { it.name !in except }.take(count)

        /** Gives each deal an id from its store and its place in the list. */
        fun deals(storeId: String, deals: List<WeeklyDeal>): List<WeeklyDeal> =
            deals.mapIndexed { index, deal -> deal.copy(id = "$storeId-${index + 1}") }

        fun deal(
            name: String,
            brand: String?,
            packageSize: String?,
            label: String?,
            price: Double?,
            unit: String? = null,
            was: Double? = null,
            card: Boolean = false,
            until: String? = null,
        ) = WeeklyDeal(
            id = "",
            name = name,
            brand = brand,
            packageSize = packageSize,
            label = label,
            price = price,
            priceUnit = unit,
            regularPrice = was,
            needsLoyaltyCard = card,
            validUntil = until,
        )
    }
}
