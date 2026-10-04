package com.top10.deals.data.hardcoded

import com.top10.deals.domain.model.GenericProduct
import com.top10.deals.domain.model.LocalizedText
import com.top10.deals.domain.model.LocalizedText.Companion.same
import com.top10.deals.domain.model.ProductCategory
import com.top10.deals.domain.model.ProductCategory.BABY
import com.top10.deals.domain.model.ProductCategory.BAKERY
import com.top10.deals.domain.model.ProductCategory.BEER
import com.top10.deals.domain.model.ProductCategory.CANNED
import com.top10.deals.domain.model.ProductCategory.CHEESE
import com.top10.deals.domain.model.ProductCategory.CLEANING
import com.top10.deals.domain.model.ProductCategory.COFFEE_TEA
import com.top10.deals.domain.model.ProductCategory.DAIRY
import com.top10.deals.domain.model.ProductCategory.EGGS
import com.top10.deals.domain.model.ProductCategory.FISH
import com.top10.deals.domain.model.ProductCategory.FROZEN
import com.top10.deals.domain.model.ProductCategory.FRUIT
import com.top10.deals.domain.model.ProductCategory.ICE_CREAM
import com.top10.deals.domain.model.ProductCategory.LAUNDRY
import com.top10.deals.domain.model.ProductCategory.MEAT
import com.top10.deals.domain.model.ProductCategory.PAPER
import com.top10.deals.domain.model.ProductCategory.PASTA
import com.top10.deals.domain.model.ProductCategory.PERSONAL_CARE
import com.top10.deals.domain.model.ProductCategory.PET
import com.top10.deals.domain.model.ProductCategory.POULTRY
import com.top10.deals.domain.model.ProductCategory.SNACKS
import com.top10.deals.domain.model.ProductCategory.SOFT_DRINKS
import com.top10.deals.domain.model.ProductCategory.SPIRITS
import com.top10.deals.domain.model.ProductCategory.SWEETS
import com.top10.deals.domain.model.ProductCategory.VEGETABLES
import com.top10.deals.domain.model.ProductCategory.WATER
import com.top10.deals.domain.model.ProductCategory.WINE
import com.top10.deals.domain.model.WeeklyDeal
import com.top10.deals.domain.repository.WeeklyDealsRepository

/**
 * This week's deals per store, typed in by hand until they are scraped, in English, French and
 * Dutch. Delhaize's are copied from its own folders and ALDI's from its own offers page: French
 * and Dutch exactly as each store prints them, English translated. The other stores' come from
 * Belgian folder sites as they listed them on 3 October 2026, are not checked against the folders,
 * and are translated by hand into all three languages; the rest are made up (see [madeUp]) to make 20.
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
            deal(POULTRY, t("Chicken breast fillets", "Filets de poulet", "Kipfilets"), null, same("600 g"), free("1+1"), 8.49, product = GenericProduct.CHICKEN_BREAST),
            deal(FISH, t("Atlantic salmon fillets", "Filets de saumon atlantique", "Atlantische zalmfilets"), null, same("250 g"), null, 4.79, was = 7.99, product = GenericProduct.SALMON),
            deal(MEAT, t("Minced beef and pork", "Haché porc et bœuf", "Gemengd gehakt"), null, same("1 kg"), null, 6.50, was = 9.29, product = GenericProduct.MINCED_MEAT),
            deal(EGGS, t("Free-range eggs", "Œufs de plein air", "Scharreleieren"), null, t("12 eggs", "12 pièces", "12 stuks"), free("2+1"), 4.19, product = GenericProduct.EGGS),
            deal(VEGETABLES, t("Vine tomatoes", "Tomates en grappe", "Trostomaten"), null, same("500 g"), null, 1.81, was = 2.79, product = GenericProduct.TOMATOES),
            deal(FRUIT, t("Jonagold apples", "Pommes Jonagold", "Jonagold appelen"), null, t("1.5 kg", "1,5 kg", "1,5 kg"), free("1+1"), 3.99, product = GenericProduct.APPLES),
            deal(FRUIT, t("Bananas", "Bananes", "Bananen"), null, same("1 kg"), null, 1.49, was = 1.99, product = GenericProduct.BANANAS),
            deal(DAIRY, t("Semi-skimmed milk", "Lait demi-écrémé", "Halfvolle melk"), null, same("6 x 1 L"), null, 5.50, was = 6.89, product = GenericProduct.MILK),
            deal(DAIRY, t("Butter", "Beurre", "Boter"), null, same("250 g"), t("2nd at half price", "2e à moitié prix", "2de aan halve prijs"), 2.99, product = GenericProduct.BUTTER),
            deal(CHEESE, t("Gouda cheese slices", "Gouda en tranches", "Goudse kaas in sneden"), null, same("400 g"), null, 3.49, was = 4.99, product = GenericProduct.CHEESE),
            deal(DAIRY, t("Greek yoghurt", "Yaourt grec", "Griekse yoghurt"), null, same("4 x 150 g"), null, 3.01, was = 4.49, product = GenericProduct.YOGHURT),
            deal(BAKERY, t("Sourdough loaf", "Pain au levain", "Zuurdesembrood"), null, same("800 g"), t("2nd at half price", "2e à moitié prix", "2de aan halve prijs"), 3.59, product = GenericProduct.BREAD),
            deal(PASTA, same("Penne rigate"), null, same("1 kg"), null, 1.73, was = 2.89, product = GenericProduct.PASTA),
            deal(PASTA, t("Lasagne bolognese", "Lasagne bolognaise", "Lasagne bolognese"), null, same("1 kg"), null, 5.24, was = 7.49, product = GenericProduct.LASAGNE),
            deal(COFFEE_TEA, t("Ground coffee", "Café moulu", "Gemalen koffie"), null, same("500 g"), null, 5.99, was = 7.99, product = GenericProduct.GROUND_COFFEE),
            deal(SOFT_DRINKS, t("Orange juice", "Jus d’orange", "Sinaasappelsap"), null, same("1 L"), free("2+1"), 2.49, product = GenericProduct.ORANGE_JUICE),
            deal(PAPER, t("Toilet paper", "Papier toilette", "Toiletpapier"), null, t("12 rolls", "12 rouleaux", "12 rollen"), null, 4.89, was = 6.99, product = GenericProduct.TOILET_PAPER),
            deal(FROZEN, t("Frozen pizza margherita", "Pizza margherita surgelée", "Diepvriespizza margherita"), null, same("3 x 350 g"), null, 5.99, was = 7.99, product = GenericProduct.PIZZA),
        )

        // Each deal starts with its category, which the app draws when it has no photo for the deal's
        // product (the last argument, where it is an everyday product we have a photo of).
        // The label is the deal as the source printed it, or null when it gave only prices.
        val DEALS: Map<String, List<WeeklyDeal>> = mapOf(
            // From Delhaize's own folders for week 40 (01/10 to 07/10/2026), French and Dutch as
            // printed. Every Delhaize promo needs a SuperPlus card, registered or not. Prices, and
            // the original prices next to them, are for the quantity in the folder's example,
            // hence the unit; the folder prints no original price for some.
            "delhaize" to deals(
                "delhaize",
                listOf(
                    deal(VEGETABLES, t("Belgian endives", "Chicons", "Witloof"), null, same("650 g"), free("1+1", fr = "gratis"), null, card = true, until = "07/10", product = GenericProduct.ENDIVES),
                    deal(VEGETABLES, t("Veggie or organic veggie product", "Produit veggie ou veggie bio", "Veggie of bioveggie product"), "Delhaize", t("150 g to 500 g", "150 g à 500 g", "Van 150 g tot 500 g"), free("1+1", fr = "gratis"), 3.09, "/2", was = 6.18, card = true, until = "07/10"),
                    deal(EGGS, t("Free-range eggs", "Œufs de poules élevées en plein air", "Eieren van hennen met vrije uitloop"), null, t("6 eggs", "6 pièces", "6 stuks"), delhaizeSecondAt("-50%"), 3.23, "/2", was = 4.30, card = true, until = "07/10", product = GenericProduct.EGGS),
                    deal(FISH, t("Jumbo mussels", "Moules jumbo", "Jumbo mosselen"), null, t("1 kg or 2 kg", "1 kg ou 2 kg", "1 kg of 2 kg"), delhaizeSecondAt("-50%"), 11.97, "/2", was = 15.96, card = true, until = "07/10", product = GenericProduct.MUSSELS),
                    deal(POULTRY, t("Chicken tenderloins", "Aiguillettes de poulet", "Kippenhaasjes"), null, VARIABLE_WEIGHT, delhaizeSecondAt("-50%"), 9.67, "/kg", was = 12.89, card = true, until = "07/10", product = GenericProduct.CHICKEN_BREAST),
                    deal(MEAT, t("Beef: chateaubriand, sirloin or rump steak", "Viande de boeuf: châteaubriand, contrefilet ou rumsteak", "Rundvlees: chateaubriand, dunne lende of rumsteak"), null, VARIABLE_WEIGHT, same("-20%"), 19.99, "/kg", was = 24.99, card = true, until = "07/10", product = GenericProduct.BEEF_STEAK),
                    deal(FISH, t("Cod loin", "Dos de cabillaud", "Kabeljauwhaasje"), null, VARIABLE_WEIGHT, delhaizeSecondAt("-40%"), 31.99, "/kg", was = 39.99, card = true, until = "07/10", product = GenericProduct.COD),
                    deal(MEAT, t("Cold cuts", "Charcuterie", "Fijne vleeswaren"), "Breydel", t("150 g, 300 g or variable weight", "150 g, 300 g ou poids variable", "150 g, 300 g of variabel gewicht"), delhaizeSecondAt("-50%"), 6.59, "/2", was = 8.78, card = true, until = "07/10", product = GenericProduct.HAM),
                    deal(CHEESE, t("Cheese", "Fromage", "Kaas"), "Oudendijk", t("180 g, 250 g or variable weight", "180 g, 250 g ou poids variable", "180 g, 250 g of variabel gewicht"), delhaizeSecondAt("-50%"), 6.74, "/2", was = 8.98, card = true, until = "07/10", product = GenericProduct.CHEESE),
                    deal(DAIRY, t("Butter or cooking cream", "Beurre ou crème culinaire", "Boter of culinaire room"), "Carlsbourg", t("200 g to 250 g, 200 ml or 250 ml", "200 g à 250 g, 200 ml ou 250 ml", "Van 200 g tot 250 g, 200 ml of 250 ml"), delhaizeSecondAt("-50%"), 3.68, "/2", was = 4.90, card = true, until = "07/10", product = GenericProduct.BUTTER),
                    deal(PASTA, t("Pasta", "Pâtes", "Pasta"), "Panzani", same("500 g"), delhaizeSecondAt("-50%"), 3.29, "/2", was = 4.38, card = true, until = "07/10", product = GenericProduct.PASTA),
                    deal(CANNED, t("Canned vegetables", "Légumes en conserve", "Groenten in blik"), "Bonduelle", t("255 g to 800 g", "255 g à 800 g", "Van 255 g tot 800 g"), free("1+1", fr = "gratis"), 2.49, "/2", was = 4.98, card = true, until = "07/10", product = GenericProduct.CANNED_FOOD),
                    deal(FROZEN, same("Pizza Holy Slice"), "Iglo", t("327 g to 570 g", "327 g à 570 g", "Van 327 g tot 570 g"), free("1+1", fr = "gratis"), 5.49, "/2", was = 10.98, card = true, until = "07/10", product = GenericProduct.PIZZA),
                    deal(FROZEN, t("Specialty fries or roast potatoes", "Frites spéciales ou patates rissolées", "Speciale frieten of gebakken aardappelen"), "Belviva", t("600 g or 750 g", "600 g ou 750 g", "600 g of 750 g"), free("1+1", fr = "gratis"), 4.49, "/2", was = 8.98, card = true, until = "07/10", product = GenericProduct.FRIES),
                    deal(COFFEE_TEA, t("Coffee beans or ground coffee", "Café en grains ou moulu", "Koffie, in bonen of gemalen"), "Lavazza", t("250 g to 1 kg", "250 g à 1 kg", "Van 250 g tot 1 kg"), delhaizeSecondAt("-70%"), 10.39, "/2", was = 15.98, card = true, until = "07/10", product = GenericProduct.COFFEE_BEANS),
                    deal(SOFT_DRINKS, t("Coca-Cola Original or Zero", "Coca-Cola Original ou Zero", "Coca-Cola Original of Zero"), "Coca-Cola", t("12 x 37.5 cl", "12 x 37,5 cl", "12 x 37,5 cl"), free("9+3", fr = "gratis"), 11.70, "/promopack", card = true, until = "07/10", product = GenericProduct.SOFT_DRINK_CANS),
                    deal(WINE, t("Over 250 wines and sparkling wines", "Plus de 250 vins et bulles", "250 wijnen en bubbels"), null, same("75 cl"), free("2+1", fr = "gratis"), null, card = true, until = "21/10", product = GenericProduct.RED_WINE),
                    deal(LAUNDRY, t("Laundry detergent or stain remover", "Produit pour la lessive ou détachant", "Wasmiddel of vlekverwijderaar"), "Dash", t("15 to 80 doses", "15 à 80 doses", "Van 15 doses tot 80 doses"), free("1+1", fr = "gratis"), 22.99, "/2", was = 45.98, card = true, until = "14/10", product = GenericProduct.LAUNDRY_DETERGENT),
                    deal(PAPER, t("Moltonel toilet paper", "Papier toilette Moltonel", "Toiletpapier Moltonel"), "Lotus", t("18 rolls", "18 rouleaux", "18 rollen"), free("12+6", fr = "gratis"), 9.32, "/promopack", card = true, until = "14/10", product = GenericProduct.TOILET_PAPER),
                    deal(BABY, t("Skin Love nappies", "Langes Skin Love", "Luiers Skin Love"), "Pampers", t("23 to 29 nappies", "23 à 29 pièces", "Van 23 tot 29 stuks"), same("-50%"), 8.00, was = 15.99, card = true, until = "07/10"),
                    deal(SWEETS, same("Donut Worry Be Happy"), null, t("Each", "Pièce", "Stuk"), free("3+3", fr = "gratis"), 3.75, "/6", was = 7.50, card = true, until = "07/10", product = GenericProduct.DONUTS),
                    deal(DAIRY, t("Apple and cinnamon yoghurt", "Yaourt pomme/cannelle", "Yoghurt appel/kaneel"), "Delhaize", same("180 g"), delhaizeSecondAt("-50%"), 1.43, "/2", was = 1.90, card = true, until = "07/10", product = GenericProduct.YOGHURT),
                ),
            ),
            // From ALDI's own offers page (aldi.be/aanbiedingen and /offres) on 4 October 2026, French
            // and Dutch as it lists them, English translated: the week 41 groceries (05/10 to 10/10)
            // and the end of the 02/10 to 08/10 offers. Sorted by how much they save.
            "aldi" to deals(
                "aldi",
                listOf(
                    deal(POULTRY, t("Chicken breast fillet", "Filet de poitrine de poulet", "Kippenborstfilet"), null, same("2 kg"), same("-43%"), 13.99, was = 24.97, until = "10/10", product = GenericProduct.CHICKEN_BREAST),
                    deal(PAPER, t("Think Pink toilet paper, 24 rolls", "Papier toilette Think pink, 24 pcs", "Think pink-toiletpapier, 24 st."), "Cosynel", t("Per pack", "Le paquet", "Per pak"), free("12+12"), 9.99, was = 19.98, until = "08/10", product = GenericProduct.TOILET_PAPER),
                    deal(LAUNDRY, t("Liquid laundry detergent", "Lessive liquide", "Vloeibaar wasmiddel"), "Dreft", t("2 x 32 washes", "2 x 32 cycles", "2 x 32 wasbeurten"), free("1+1"), 10.49, was = 20.98, until = "08/10", product = GenericProduct.LAUNDRY_DETERGENT),
                    deal(MEAT, t("Irish steak", "Steak irlandais", "Ierse steak"), null, same("2 x 300 g"), free("1+1"), 8.69, was = 17.38, until = "10/10", product = GenericProduct.BEEF_STEAK),
                    deal(POULTRY, t("Chicken chipolatas", "Chipolatas de poulet", "Kippenchipolata's"), null, same("2 x 1 kg"), free("1+1"), 8.69, was = 17.38, until = "10/10", product = GenericProduct.SAUSAGES),
                    deal(VEGETABLES, t("Potatoes for chips", "Pommes de terre pour frites", "Frietaardappelen"), null, same("2 x 5 kg"), free("1+1"), 7.99, was = 15.98, until = "10/10", product = GenericProduct.POTATOES),
                    deal(SWEETS, t("Cereal bars, 12 pieces", "Barres aux céréales, 12 pcs", "Graanrepen, 12 st."), "Nesquik", same("12 x 25 g"), free("6+6"), 3.99, was = 7.98, until = "08/10", product = GenericProduct.CEREAL_BARS),
                    deal(FROZEN, t("Cheese croquettes, 4 pieces", "Croquettes de fromage, 4 pcs", "Kaaskroketten, 4 st."), "Mora", same("2 x (4 x 70 g)"), t("2nd at -50%", "2e à -50%", "2e aan -50%"), 9.58, was = 12.78, until = "08/10", product = GenericProduct.CROQUETTES),
                    deal(COFFEE_TEA, t("Instant coffee, dessert", "Café soluble dessert", "Oploskoffie dessert"), "Nescafé", same("2 x 190 g"), t("2nd at -50%", "2e à -50%", "2e aan -50%"), 12.73, was = 16.98, until = "08/10", product = GenericProduct.INSTANT_COFFEE),
                    deal(SOFT_DRINKS, t("Coca-Cola regular, 24 cans", "Coca-Cola regular, 24 pcs", "Coca-Cola regular, 24 st."), "Coca-Cola", same("24 x 25 cl"), free("18+6"), 11.45, was = 15.27, until = "08/10", product = GenericProduct.SOFT_DRINK_CANS),
                    deal(FISH, t("Scallops", "Noix de Saint-Jacques", "Sint-jakobsnoten"), "Gourmet Finest Cuisine", same("200 g"), same("-30%"), 6.99, was = 9.99, until = "10/10", product = GenericProduct.SCALLOPS),
                    deal(POULTRY, t("Chicken fillet strips", "Lamelles de poulet", "Kipfiletreepjes"), null, same("500 g"), same("-30%"), 4.89, was = 6.99, until = "10/10", product = GenericProduct.CHICKEN_BREAST),
                    deal(VEGETABLES, t("Onions", "Oignons", "Uien"), null, same("2 kg"), same("-30%"), 1.79, was = 2.59, until = "10/10", product = GenericProduct.ONIONS),
                    deal(FRUIT, t("White grapes with seeds", "Raisins blancs avec pépins", "Witte druiven met pit"), null, same("750 g"), same("-30%"), 1.99, was = 2.85, until = "10/10", product = GenericProduct.WHITE_GRAPES),
                    deal(FRUIT, t("Seedless black grapes", "Raisins noirs sans pépins", "Pitloze blauwe druiven"), null, same("500 g"), same("-31%"), 1.50, was = 2.19, until = "10/10", product = GenericProduct.BLACK_GRAPES),
                    deal(FRUIT, t("Strawberries", "Fraises", "Aardbeien"), null, same("2 x 500 g"), t("2nd at -50%", "2e à -50%", "2de aan -50%"), 6.73, was = 8.98, until = "10/10", product = GenericProduct.STRAWBERRIES),
                    deal(FRUIT, t("Mango", "Mangue", "Mango"), null, t("2 x 1 piece", "2 x 1 pce", "2 x 1 st."), t("2nd at -50%", "2e à -50%", "2de aan -50%"), 2.83, was = 3.78, until = "10/10", product = GenericProduct.MANGO),
                    deal(FISH, t("Fresh salmon with skin, 8 pieces", "Saumon frais avec peau, 8 pcs", "Verse zalm met huid, 8 st."), "Golden Seafood", same("8 x 125 g"), same("-19%"), 16.99, was = 21.20, until = "10/10", product = GenericProduct.SALMON),
                    deal(VEGETABLES, t("Brussels sprouts", "Choux de Bruxelles", "Spruiten"), null, same("1 kg"), same("-25%"), 2.29, was = 3.05, until = "10/10", product = GenericProduct.BRUSSELS_SPROUTS),
                    deal(BAKERY, t("Brown tiger bread", "Pain gris tigré", "Bruin tijgerbrood"), null, same("800 g"), same("-25%"), 1.49, was = 1.99, until = "10/10", product = GenericProduct.BREAD),
                    deal(CHEESE, t("Grated Emmental", "Emmental râpé", "Geraspte emmental"), "Milsani", same("500 g"), same("-25%"), 2.99, was = 3.99, until = "08/10"),
                    deal(BEER, t("Pils, 18 bottles", "Pils, 18 pcs", "Pils, 18 st."), "Jupiler", t("18 x 35.5 cl", "18 x 35,5 cl", "18 x 35,5 cl"), free("15+3"), 15.99, was = 19.19, until = "08/10", product = GenericProduct.BEER),
                    deal(WINE, t("Organic red wine 'Esteban'", "Vin rouge bio 'Esteban'", "Rode biowijn 'Esteban'"), null, same("2 x 75 cl"), t("2nd at -50%", "2e à -50%", "2e aan -50%"), 7.48, was = 9.98, until = "08/10", product = GenericProduct.RED_WINE),
                    deal(VEGETABLES, t("Broccoli", "Brocoli", "Broccoli"), null, same("500 g"), same("-22%"), 1.00, was = 1.29, until = "10/10", product = GenericProduct.BROCCOLI),
                    deal(VEGETABLES, t("Butternut squash", "Courge butternut", "Butternutpompoen"), null, t("Per kg", "Le kg", "Per kg"), same("-20%"), 1.50, "/kg", was = 1.89, until = "10/10", product = GenericProduct.SQUASH),
                ),
            ),
            // Week 40 folder (28/09 to 03/10) and week 41 folder (05/10 to 10/10).
            "lidl" to deals(
                "lidl",
                listOf(
                    deal(POULTRY, t("Chicken breast fillet XXL", "Filet de poulet XXL", "Kipfilet XXL"), null, t("1.5 kg", "1,5 kg", "1,5 kg"), same("-31%"), 9.99, until = "03/10", product = GenericProduct.CHICKEN_BREAST),
                    deal(SOFT_DRINKS, t("Energy drink", "Boisson énergisante", "Energiedrank"), "Red Bull", same("2 x 6 x 25 cl"), t("2nd at -50%", "2e à -50%", "2de aan -50%"), 7.49, until = "10/10", product = GenericProduct.SOFT_DRINK_CANS),
                    deal(SOFT_DRINKS, t("Lemonade", "Limonade", "Limonade"), "Schweppes", same("2 x 6 x 50 cl"), null, null, until = "10/10", product = GenericProduct.LEMONADE),
                    deal(SOFT_DRINKS, t("Energy drink", "Boisson énergisante", "Energiedrank"), "Monster", t("3 x 55.3 cl", "3 x 55,3 cl", "3 x 55,3 cl"), null, 3.00, until = "03/10", product = GenericProduct.SOFT_DRINK_CANS),
                    deal(LAUNDRY, t("Laundry detergent or softener", "Lessive ou adoucissant", "Wasmiddel of wasverzachter"), "Dreft", t("2 x 32 washes", "2 x 32 lavages", "2 x 32 wasbeurten"), null, 5.69, until = "03/10", product = GenericProduct.LAUNDRY_DETERGENT),
                    deal(LAUNDRY, t("Liquid laundry detergent", "Lessive liquide", "Vloeibaar wasmiddel"), "Ariel", t("2 x 25 washes", "2 x 25 lavages", "2 x 25 wasbeurten"), null, 22.99, until = "10/10", product = GenericProduct.LAUNDRY_DETERGENT),
                ) + madeUp(14, except = setOf("Chicken breast fillets")),
            ),
            // Loyalty card offers valid through October.
            "intermarche" to deals(
                "intermarche",
                listOf(
                    deal(COFFEE_TEA, t("Coffee pads", "Dosettes de café", "Koffiepads"), "Lavazza", null, t("€4.00 off per 2, €8.00 on Thursdays", "-4,00 € par 2, -8,00 € le jeudi", "€ 4,00 korting per 2, € 8,00 op donderdag"), null, card = true, until = "31/10", product = GenericProduct.COFFEE_CAPSULES),
                    deal(COFFEE_TEA, t("Starbucks capsules", "Capsules Starbucks", "Starbucks-capsules"), "Nescafé Dolce Gusto", null, t("€1.50 off per 2, €3.00 on Thursdays", "-1,50 € par 2, -3,00 € le jeudi", "€ 1,50 korting per 2, € 3,00 op donderdag"), null, card = true, until = "31/10", product = GenericProduct.COFFEE_CAPSULES),
                    deal(LAUNDRY, t("Liquid laundry detergent", "Lessive liquide", "Vloeibaar wasmiddel"), "Fleuril", null, t("€4.00 off per 2", "-4,00 € par 2", "€ 4,00 korting per 2"), null, card = true, until = "31/10", product = GenericProduct.LAUNDRY_DETERGENT),
                ) + madeUp(17, except = setOf("Ground coffee")),
            ),
            // Folder valid 24/09 to 07/10.
            "spar" to deals(
                "spar",
                listOf(
                    deal(FISH, t("Jumbo mussels", "Moules jumbo", "Jumbo mosselen"), null, same("1 kg"), null, 7.00, until = "07/10", product = GenericProduct.MUSSELS),
                    deal(FRUIT, t("Jonagold apples", "Pommes Jonagold", "Jonagold appelen"), null, same("4 x 800 g"), null, null, until = "07/10", product = GenericProduct.APPLES),
                    deal(DAIRY, t("Extra protein milk", "Lait extra protéines", "Melk extra proteïne"), "Campina", same("2 x 1 L"), free("1+1"), 1.89, until = "07/10", product = GenericProduct.MILK),
                    deal(DAIRY, t("Greek-style yoghurt", "Yaourt à la grecque", "Yoghurt op Griekse wijze"), "Danone", same("2 x 440 g"), null, 2.29, until = "07/10", product = GenericProduct.YOGHURT),
                    deal(DAIRY, t("Yoghurt, plain, strawberry or lemon", "Yaourt nature, fraise ou citron", "Yoghurt natuur, aardbei of citroen"), "Danone", null, free("1+1"), null, until = "07/10", product = GenericProduct.YOGHURT),
                    deal(CHEESE, same("Ricotta"), "Spar", same("250 g"), free("1+1"), null, until = "07/10"),
                    deal(PASTA, t("Pasta", "Pâtes", "Pasta"), "De Cecco", t("250 g or 500 g", "250 g ou 500 g", "250 g of 500 g"), free("2+2"), 4.36, until = "07/10", product = GenericProduct.PASTA),
                    deal(FRUIT, t("Mango", "Mangue", "Mango"), null, t("1 piece", "1 pièce", "1 stuk"), free("1+1"), null, until = "07/10", product = GenericProduct.MANGO),
                    deal(SOFT_DRINKS, t("Coca-Cola, Fanta, Sprite, Fuze Tea, Aquarius or Powerade", "Coca-Cola, Fanta, Sprite, Fuze Tea, Aquarius ou Powerade", "Coca-Cola, Fanta, Sprite, Fuze Tea, Aquarius of Powerade"), null, null, null, 1.50, until = "07/10", product = GenericProduct.SOFT_DRINK_CANS),
                    deal(SNACKS, t("Protein cereal bars, cocoa", "Barres de céréales protéinées, cacao", "Proteïnerepen met granen, cacao"), "Nestlé Fitness", same("4 x 20 g"), free("1+1"), null, until = "07/10", product = GenericProduct.CEREAL_BARS),
                    deal(ICE_CREAM, t("Ice cream", "Crème glacée", "IJs"), "Ben & Jerry's", null, free("1+1"), null, until = "07/10", product = GenericProduct.ICE_CREAM),
                    deal(PERSONAL_CARE, t("Toothpaste", "Dentifrice", "Tandpasta"), "Sensodyne", null, free("2+2"), null, until = "07/10", product = GenericProduct.TOOTHPASTE),
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
            category: ProductCategory,
            name: LocalizedText,
            brand: String?,
            packageSize: LocalizedText?,
            label: LocalizedText?,
            price: Double?,
            unit: String? = null,
            was: Double? = null,
            card: Boolean = false,
            until: String? = null,
            product: GenericProduct? = null,
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
            category = category,
            product = product,
            validUntil = until,
        )
    }
}
