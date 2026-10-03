package com.top10.products.data.hardcoded

import com.top10.products.domain.model.LocalizedText
import com.top10.products.domain.model.LocalizedText.Companion.same
import com.top10.products.domain.model.WeeklyDeal
import com.top10.products.domain.repository.WeeklyDealsRepository

/**
 * This week's deals per store, typed in by hand until they are scraped, in English, French and
 * Dutch. Delhaize's are copied from its own folders: French and Dutch exactly as each folder
 * prints them, English translated. The other stores' come from Belgian folder sites as they listed
 * them on 3 October 2026, are not checked against the folders, and are translated by hand into all
 * three languages; the rest are made up (see [madeUp]) to make 20.
 */
class HardcodedWeeklyDeals : WeeklyDealsRepository {

    override suspend fun weeklyDeals(storeId: String): List<WeeklyDeal> =
        DEALS[storeId] ?: error("No weekly deals for $storeId")

    private companion object {
        fun t(en: String, fr: String, nl: String) = LocalizedText(en = en, fr = fr, nl = nl)

        // Deal labels. Delhaize prints "gratis" in its French folder too.
        fun free(deal: String, fr: String = "gratuit") = t("$deal free", "$deal $fr", "$deal gratis")
        fun delhaizeSecondAt(percent: String) = t("2nd at $percent", "2e à $percent", "2de tegen $percent")
        val VARIABLE_WEIGHT = t("Variable weight", "Poids variable", "Variabel gewicht")

        // Declared before DEALS, which reads it while the companion is initialised.
        /** Made-up staples that pad a store's list to 20 where the folder sites showed fewer. */
        val MADE_UP = listOf(
            deal(t("Chicken breast fillets", "Filets de poulet", "Kipfilets"), null, same("600 g"), free("1+1"), 8.49),
            deal(t("Atlantic salmon fillets", "Filets de saumon atlantique", "Atlantische zalmfilets"), null, same("250 g"), null, 4.79, was = 7.99),
            deal(t("Minced beef and pork", "Haché porc et bœuf", "Gemengd gehakt"), null, same("1 kg"), null, 6.50, was = 9.29),
            deal(t("Free-range eggs", "Œufs de plein air", "Scharreleieren"), null, t("12 eggs", "12 pièces", "12 stuks"), free("2+1"), 4.19),
            deal(t("Vine tomatoes", "Tomates en grappe", "Trostomaten"), null, same("500 g"), null, 1.81, was = 2.79),
            deal(t("Jonagold apples", "Pommes Jonagold", "Jonagold appelen"), null, t("1.5 kg", "1,5 kg", "1,5 kg"), free("1+1"), 3.99),
            deal(t("Bananas", "Bananes", "Bananen"), null, same("1 kg"), null, 1.49, was = 1.99),
            deal(t("Semi-skimmed milk", "Lait demi-écrémé", "Halfvolle melk"), null, same("6 x 1 L"), null, 5.50, was = 6.89),
            deal(t("Butter", "Beurre", "Boter"), null, same("250 g"), t("2nd at half price", "2e à moitié prix", "2de aan halve prijs"), 2.99),
            deal(t("Gouda cheese slices", "Gouda en tranches", "Goudse kaas in sneden"), null, same("400 g"), null, 3.49, was = 4.99),
            deal(t("Greek yoghurt", "Yaourt grec", "Griekse yoghurt"), null, same("4 x 150 g"), null, 3.01, was = 4.49),
            deal(t("Sourdough loaf", "Pain au levain", "Zuurdesembrood"), null, same("800 g"), t("2nd at half price", "2e à moitié prix", "2de aan halve prijs"), 3.59),
            deal(same("Penne rigate"), null, same("1 kg"), null, 1.73, was = 2.89),
            deal(t("Lasagne bolognese", "Lasagne bolognaise", "Lasagne bolognese"), null, same("1 kg"), null, 5.24, was = 7.49),
            deal(t("Ground coffee", "Café moulu", "Gemalen koffie"), null, same("500 g"), null, 5.99, was = 7.99),
            deal(t("Orange juice", "Jus d’orange", "Sinaasappelsap"), null, same("1 L"), free("2+1"), 2.49),
            deal(t("Toilet paper", "Papier toilette", "Toiletpapier"), null, t("12 rolls", "12 rouleaux", "12 rollen"), null, 4.89, was = 6.99),
            deal(t("Frozen pizza margherita", "Pizza margherita surgelée", "Diepvriespizza margherita"), null, same("3 x 350 g"), null, 5.99, was = 7.99),
        )

        // The label is the deal as the source printed it, or null when it gave only prices.
        val DEALS: Map<String, List<WeeklyDeal>> = mapOf(
            // From Delhaize's own folders for week 40 (01/10 to 07/10/2026), French and Dutch as
            // printed. Every Delhaize promo needs a SuperPlus card, registered or not. Prices, and
            // the original prices next to them, are for the quantity in the folder's example,
            // hence the unit; the folder prints no original price for some.
            "delhaize" to deals(
                "delhaize",
                listOf(
                    deal(t("Belgian endives", "Chicons", "Witloof"), null, same("650 g"), free("1+1", fr = "gratis"), null, card = true, until = "07/10"),
                    deal(t("Veggie or organic veggie product", "Produit veggie ou veggie bio", "Veggie of bioveggie product"), "Delhaize", t("150 g to 500 g", "150 g à 500 g", "Van 150 g tot 500 g"), free("1+1", fr = "gratis"), 3.09, "/2", was = 6.18, card = true, until = "07/10"),
                    deal(t("Free-range eggs", "Œufs de poules élevées en plein air", "Eieren van hennen met vrije uitloop"), null, t("6 eggs", "6 pièces", "6 stuks"), delhaizeSecondAt("-50%"), 3.23, "/2", was = 4.30, card = true, until = "07/10"),
                    deal(t("Jumbo mussels", "Moules jumbo", "Jumbo mosselen"), null, t("1 kg or 2 kg", "1 kg ou 2 kg", "1 kg of 2 kg"), delhaizeSecondAt("-50%"), 11.97, "/2", was = 15.96, card = true, until = "07/10"),
                    deal(t("Chicken tenderloins", "Aiguillettes de poulet", "Kippenhaasjes"), null, VARIABLE_WEIGHT, delhaizeSecondAt("-50%"), 9.67, "/kg", was = 12.89, card = true, until = "07/10"),
                    deal(t("Beef: chateaubriand, sirloin or rump steak", "Viande de boeuf: châteaubriand, contrefilet ou rumsteak", "Rundvlees: chateaubriand, dunne lende of rumsteak"), null, VARIABLE_WEIGHT, same("-20%"), 19.99, "/kg", was = 24.99, card = true, until = "07/10"),
                    deal(t("Cod loin", "Dos de cabillaud", "Kabeljauwhaasje"), null, VARIABLE_WEIGHT, delhaizeSecondAt("-40%"), 31.99, "/kg", was = 39.99, card = true, until = "07/10"),
                    deal(t("Cold cuts", "Charcuterie", "Fijne vleeswaren"), "Breydel", t("150 g, 300 g or variable weight", "150 g, 300 g ou poids variable", "150 g, 300 g of variabel gewicht"), delhaizeSecondAt("-50%"), 6.59, "/2", was = 8.78, card = true, until = "07/10"),
                    deal(t("Cheese", "Fromage", "Kaas"), "Oudendijk", t("180 g, 250 g or variable weight", "180 g, 250 g ou poids variable", "180 g, 250 g of variabel gewicht"), delhaizeSecondAt("-50%"), 6.74, "/2", was = 8.98, card = true, until = "07/10"),
                    deal(t("Butter or cooking cream", "Beurre ou crème culinaire", "Boter of culinaire room"), "Carlsbourg", t("200 g to 250 g, 200 ml or 250 ml", "200 g à 250 g, 200 ml ou 250 ml", "Van 200 g tot 250 g, 200 ml of 250 ml"), delhaizeSecondAt("-50%"), 3.68, "/2", was = 4.90, card = true, until = "07/10"),
                    deal(t("Pasta", "Pâtes", "Pasta"), "Panzani", same("500 g"), delhaizeSecondAt("-50%"), 3.29, "/2", was = 4.38, card = true, until = "07/10"),
                    deal(t("Canned vegetables", "Légumes en conserve", "Groenten in blik"), "Bonduelle", t("255 g to 800 g", "255 g à 800 g", "Van 255 g tot 800 g"), free("1+1", fr = "gratis"), 2.49, "/2", was = 4.98, card = true, until = "07/10"),
                    deal(same("Pizza Holy Slice"), "Iglo", t("327 g to 570 g", "327 g à 570 g", "Van 327 g tot 570 g"), free("1+1", fr = "gratis"), 5.49, "/2", was = 10.98, card = true, until = "07/10"),
                    deal(t("Specialty fries or roast potatoes", "Frites spéciales ou patates rissolées", "Speciale frieten of gebakken aardappelen"), "Belviva", t("600 g or 750 g", "600 g ou 750 g", "600 g of 750 g"), free("1+1", fr = "gratis"), 4.49, "/2", was = 8.98, card = true, until = "07/10"),
                    deal(t("Coffee beans or ground coffee", "Café en grains ou moulu", "Koffie, in bonen of gemalen"), "Lavazza", t("250 g to 1 kg", "250 g à 1 kg", "Van 250 g tot 1 kg"), delhaizeSecondAt("-70%"), 10.39, "/2", was = 15.98, card = true, until = "07/10"),
                    deal(t("Coca-Cola Original or Zero", "Coca-Cola Original ou Zero", "Coca-Cola Original of Zero"), "Coca-Cola", t("12 x 37.5 cl", "12 x 37,5 cl", "12 x 37,5 cl"), free("9+3", fr = "gratis"), 11.70, "/promopack", card = true, until = "07/10"),
                    deal(t("Over 250 wines and sparkling wines", "Plus de 250 vins et bulles", "250 wijnen en bubbels"), null, same("75 cl"), free("2+1", fr = "gratis"), null, card = true, until = "21/10"),
                    deal(t("Laundry detergent or stain remover", "Produit pour la lessive ou détachant", "Wasmiddel of vlekverwijderaar"), "Dash", t("15 to 80 doses", "15 à 80 doses", "Van 15 doses tot 80 doses"), free("1+1", fr = "gratis"), 22.99, "/2", was = 45.98, card = true, until = "14/10"),
                    deal(t("Moltonel toilet paper", "Papier toilette Moltonel", "Toiletpapier Moltonel"), "Lotus", t("18 rolls", "18 rouleaux", "18 rollen"), free("12+6", fr = "gratis"), 9.32, "/promopack", card = true, until = "14/10"),
                    deal(t("Skin Love nappies", "Langes Skin Love", "Luiers Skin Love"), "Pampers", t("23 to 29 nappies", "23 à 29 pièces", "Van 23 tot 29 stuks"), same("-50%"), 8.00, was = 15.99, card = true, until = "07/10"),
                    deal(same("Donut Worry Be Happy"), null, t("Each", "Pièce", "Stuk"), free("3+3", fr = "gratis"), 3.75, "/6", was = 7.50, card = true, until = "07/10"),
                    deal(t("Apple and cinnamon yoghurt", "Yaourt pomme/cannelle", "Yoghurt appel/kaneel"), "Delhaize", same("180 g"), delhaizeSecondAt("-50%"), 1.43, "/2", was = 1.90, card = true, until = "07/10"),
                ),
            ),
            // Folder valid 28/09 to 03/10. ALDI prints most deals as a price only.
            "aldi" to deals(
                "aldi",
                listOf(
                    deal(t("Avocados", "Avocats", "Avocado’s"), null, t("3 pieces", "3 pièces", "3 stuks"), null, 2.58, was = 3.87, until = "03/10"),
                    deal(t("Mini chicory", "Mini-chicons", "Miniwitloof"), null, null, same("-29%"), null, until = "03/10"),
                    deal(t("Hamburgers, family pack", "Hamburgers, colis familial", "Hamburgers, colli"), null, null, null, 8.99, until = "03/10"),
                    deal(same("Kiwi Sungold Jumbo"), "Zespri", null, null, 3.29, until = "03/10"),
                    deal(t("Grated Emmental", "Emmental râpé", "Geraspte emmentaler"), "Milsani", null, null, 2.99, until = "03/10"),
                    deal(t("Fish fingers", "Bâtonnets de poisson", "Vissticks"), "Iglo", null, null, 6.38, until = "03/10"),
                    deal(t("Chicken and turkey crunchies", "Crunchies poulet-dinde", "Crunchies kip-kalkoen"), null, t("10 pieces", "10 pièces", "10 stuks"), null, null, until = "03/10"),
                    deal(t("Cola zero sugar", "Cola zéro sucre", "Cola zero suiker"), "Pepsi", t("6 x 1.5 L", "6 x 1,5 L", "6 x 1,5 L"), null, 8.99, until = "03/10"),
                    deal(t("Mineral water", "Eau minérale", "Mineraalwater"), "Evian", null, null, 13.39, until = "03/10"),
                    deal(t("Beer, cold grip", "Bière, cold grip", "Bier, cold grip"), "Stella Artois", null, null, 12.13, until = "03/10"),
                    deal(t("Liquid laundry detergent", "Lessive liquide", "Vloeibaar wasmiddel"), "Dreft", null, free("1+1"), 10.49, until = "03/10"),
                    deal(t("Crisps", "Chips", "Chips"), "Sun Snacks", null, null, 1.99, until = "03/10"),
                    deal(t("Mixed snacks", "Snacks mixtes", "Gemengde snacks"), "Sun Snacks", null, null, 3.00, until = "03/10"),
                    deal(t("Ginger beer", "Ginger beer", "Gemberbier"), "River", null, null, 3.99, until = "03/10"),
                    deal(t("Baby wipes Sensitive", "Lingettes bébé Sensitive", "Babydoekjes Sensitive"), "Mamia", t("80 wipes", "80 pièces", "80 stuks"), t("+20 points with the app", "+20 points avec l’app", "+20 punten met de app"), null, card = true, until = "03/10"),
                    deal(t("Intimate wash", "Soin lavant intime", "Intieme wasemulsie"), "Lactacyd", same("2 x 300 ml"), free("1+1"), 11.99, until = "03/10"),
                    deal(t("Dry dog food", "Croquettes pour chiens", "Droge hondenvoeding"), "Romeo", null, null, 2.99, until = "03/10"),
                    deal(t("Dental sticks for dogs", "Bâtonnets dentaires pour chiens", "Kauwstaafjes voor honden"), "Romeo", null, null, 2.99, until = "03/10"),
                    deal(t("Cream liqueur", "Liqueur à la crème", "Roomlikeur"), "Blackstone", null, null, 8.99, until = "03/10"),
                    deal(t("Denture cleaning tablets", "Comprimés nettoyants pour prothèses dentaires", "Reinigingstabletten voor kunstgebit"), "Steradent", null, null, 5.99, until = "03/10"),
                    deal(t("1-2-Spray mop", "Balai 1-2-Spray", "1-2-Spray-vloerwisser"), "Vileda", null, null, 16.99, until = "03/10"),
                    deal(t("Diet tea", "Thé minceur", "Dieetthee"), "Juvamine", null, null, 2.99, until = "03/10"),
                ),
            ),
            // Week 40 folder (28/09 to 03/10) and week 41 folder (05/10 to 10/10).
            "lidl" to deals(
                "lidl",
                listOf(
                    deal(t("Chicken breast fillet XXL", "Filet de poulet XXL", "Kipfilet XXL"), null, t("1.5 kg", "1,5 kg", "1,5 kg"), same("-31%"), 9.99, until = "03/10"),
                    deal(t("Energy drink", "Boisson énergisante", "Energiedrank"), "Red Bull", same("2 x 6 x 25 cl"), t("2nd at -50%", "2e à -50%", "2de aan -50%"), 7.49, until = "10/10"),
                    deal(t("Lemonade", "Limonade", "Limonade"), "Schweppes", same("2 x 6 x 50 cl"), null, null, until = "10/10"),
                    deal(t("Energy drink", "Boisson énergisante", "Energiedrank"), "Monster", t("3 x 55.3 cl", "3 x 55,3 cl", "3 x 55,3 cl"), null, 3.00, until = "03/10"),
                    deal(t("Laundry detergent or softener", "Lessive ou adoucissant", "Wasmiddel of wasverzachter"), "Dreft", t("2 x 32 washes", "2 x 32 lavages", "2 x 32 wasbeurten"), null, 5.69, until = "03/10"),
                    deal(t("Liquid laundry detergent", "Lessive liquide", "Vloeibaar wasmiddel"), "Ariel", t("2 x 25 washes", "2 x 25 lavages", "2 x 25 wasbeurten"), null, 22.99, until = "10/10"),
                ) + madeUp(14, except = setOf("Chicken breast fillets")),
            ),
            // Loyalty card offers valid through October.
            "intermarche" to deals(
                "intermarche",
                listOf(
                    deal(t("Coffee pads", "Dosettes de café", "Koffiepads"), "Lavazza", null, t("€4.00 off per 2, €8.00 on Thursdays", "-4,00 € par 2, -8,00 € le jeudi", "€ 4,00 korting per 2, € 8,00 op donderdag"), null, card = true, until = "31/10"),
                    deal(t("Starbucks capsules", "Capsules Starbucks", "Starbucks-capsules"), "Nescafé Dolce Gusto", null, t("€1.50 off per 2, €3.00 on Thursdays", "-1,50 € par 2, -3,00 € le jeudi", "€ 1,50 korting per 2, € 3,00 op donderdag"), null, card = true, until = "31/10"),
                    deal(t("Liquid laundry detergent", "Lessive liquide", "Vloeibaar wasmiddel"), "Fleuril", null, t("€4.00 off per 2", "-4,00 € par 2", "€ 4,00 korting per 2"), null, card = true, until = "31/10"),
                ) + madeUp(17, except = setOf("Ground coffee")),
            ),
            // Folder valid 24/09 to 07/10.
            "spar" to deals(
                "spar",
                listOf(
                    deal(t("Jumbo mussels", "Moules jumbo", "Jumbo mosselen"), null, same("1 kg"), null, 7.00, until = "07/10"),
                    deal(t("Jonagold apples", "Pommes Jonagold", "Jonagold appelen"), null, same("4 x 800 g"), null, null, until = "07/10"),
                    deal(t("Extra protein milk", "Lait extra protéines", "Melk extra proteïne"), "Campina", same("2 x 1 L"), free("1+1"), 1.89, until = "07/10"),
                    deal(t("Greek-style yoghurt", "Yaourt à la grecque", "Yoghurt op Griekse wijze"), "Danone", same("2 x 440 g"), null, 2.29, until = "07/10"),
                    deal(t("Yoghurt, plain, strawberry or lemon", "Yaourt nature, fraise ou citron", "Yoghurt natuur, aardbei of citroen"), "Danone", null, free("1+1"), null, until = "07/10"),
                    deal(same("Ricotta"), "Spar", same("250 g"), free("1+1"), null, until = "07/10"),
                    deal(t("Pasta", "Pâtes", "Pasta"), "De Cecco", t("250 g or 500 g", "250 g ou 500 g", "250 g of 500 g"), free("2+2"), 4.36, until = "07/10"),
                    deal(t("Mango", "Mangue", "Mango"), null, t("1 piece", "1 pièce", "1 stuk"), free("1+1"), null, until = "07/10"),
                    deal(t("Coca-Cola, Fanta, Sprite, Fuze Tea, Aquarius or Powerade", "Coca-Cola, Fanta, Sprite, Fuze Tea, Aquarius ou Powerade", "Coca-Cola, Fanta, Sprite, Fuze Tea, Aquarius of Powerade"), null, null, null, 1.50, until = "07/10"),
                    deal(t("Protein cereal bars, cocoa", "Barres de céréales protéinées, cacao", "Proteïnerepen met granen, cacao"), "Nestlé Fitness", same("4 x 20 g"), free("1+1"), null, until = "07/10"),
                    deal(t("Ice cream", "Crème glacée", "IJs"), "Ben & Jerry's", null, free("1+1"), null, until = "07/10"),
                    deal(t("Toothpaste", "Dentifrice", "Tandpasta"), "Sensodyne", null, free("2+2"), null, until = "07/10"),
                ) + madeUp(8, except = setOf("Jonagold apples", "Greek yoghurt", "Penne rigate")),
            ),
        )

        /** [count] made-up staples, leaving out those named (in English) in [except]. */
        fun madeUp(count: Int, except: Set<String> = emptySet()): List<WeeklyDeal> =
            MADE_UP.filter { it.name.en !in except }.take(count)

        /** Gives each deal an id from its store and its place in the list. */
        fun deals(storeId: String, deals: List<WeeklyDeal>): List<WeeklyDeal> =
            deals.mapIndexed { index, deal -> deal.copy(id = "$storeId-${index + 1}") }

        fun deal(
            name: LocalizedText,
            brand: String?,
            packageSize: LocalizedText?,
            label: LocalizedText?,
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
