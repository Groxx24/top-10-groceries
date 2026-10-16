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
 * This week's deals per store, in English, French and Dutch. Lidl's are written by
 * `tools/lidl_deals.py` from lidl.be into [LIDL_WEEKLY_DEALS]. The others are typed in by hand:
 * Delhaize's copied from its own folders and ALDI's from its own offers page, French and Dutch
 * exactly as each store prints them, English translated. The other stores' come from Belgian
 * folder sites as they listed them on 3 October 2026, are not checked against the folders, and are
 * translated by hand into all three languages; the rest are made up (see [madeUp]) to make 20.
 */
class HardcodedWeeklyDeals : WeeklyDealsRepository {

    override suspend fun weeklyDeals(storeId: String): List<WeeklyDeal> =
        DEALS[storeId] ?: error("No weekly deals for $storeId")

    private companion object {
        fun t(en: String, fr: String, nl: String) = LocalizedText(en = en, fr = fr, nl = nl)

        // Deal labels. Delhaize prints "gratis" in its French folder too.
        fun free(deal: String, fr: String = "gratuit") = t("$deal free", "$deal $fr", "$deal gratis")
        fun delhaizeSecondAt(percent: String) = t("2nd at $percent", "2e à $percent", "2de tegen $percent")
        val SECOND_TRAY_HALF_PRICE = t("2nd tray at -50%", "2e ravier à -50%", "2de schaal tegen -50%")

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
            // From Delhaize's own folders for week 42 (15/10 to 21/10/2026), French and Dutch as
            // printed. Every Delhaize promo needs a SuperPlus card, registered or not. Prices, and
            // the original prices next to them, are for the quantity in the folder's example,
            // hence the unit.
            "delhaize" to deals(
                "delhaize",
                listOf(
                    deal(POULTRY, t("Diced chicken fillet", "Dés de filet de poulet", "Kipfiletblokjes"), "Delhaize", null, t("1+1 tray free", "1+1 ravier gratis", "1+1 schaal gratis"), 7.00, "/kg", was = 13.99, card = true, until = "21/10", product = GenericProduct.CHICKEN_BREAST),
                    deal(CHEESE, t("Cheese spread or sliced cheese", "Fromage en crème ou en tranches", "Kaascrème of kaas in sneden"), "Old Amsterdam", t("125 g or 150 g", "125 g ou 150 g", "125 g of 150 g"), free("1+1", fr = "gratis"), 3.15, "/2", was = 6.30, card = true, until = "21/10", product = GenericProduct.CHEESE),
                    deal(PASTA, t("Pasta", "Pâtes", "Pasta"), "La Molisana", same("500 g"), free("1+1", fr = "gratis"), 2.15, "/2", was = 4.30, card = true, until = "21/10", product = GenericProduct.PASTA),
                    deal(COFFEE_TEA, t("Ground coffee or pads", "Café moulu ou en pads", "Koffie, gemalen of in pads"), "Perla", t("250 g or 36 pads", "250 g ou 36 pads", "250 g of 36 pads"), free("1+1", fr = "gratis"), 4.99, "/2", was = 9.98, card = true, until = "21/10", product = GenericProduct.GROUND_COFFEE),
                    deal(DAIRY, t("Kefir", "Kéfir", "Kefir"), "Activia", t("4 x 125 g to 345 g, 280 ml or 500 ml", "De 4 x 125 g à 345 g, 280 ml ou 500 ml", "Van 4 x 125 g tot 345 g, 280 ml of 500 ml"), free("1+1", fr = "gratis"), 2.49, "/2", was = 4.98, card = true, until = "21/10", product = GenericProduct.YOGHURT),
                    deal(VEGETABLES, t("Hesbaye carrots", "Carottes de Hesbaye", "Haspengouwse wortelen"), "Delhaize", same("1 kg"), delhaizeSecondAt("-50%"), 2.24, "/2", was = 2.98, card = true, until = "21/10", product = GenericProduct.CARROTS),
                    deal(VEGETABLES, t("Trimmed Brussels sprouts", "Choux de Bruxelles nettoyés", "Geschoonde spruiten"), "Delhaize", same("500 g"), delhaizeSecondAt("-50%"), 3.74, "/2", was = 4.98, card = true, until = "21/10", product = GenericProduct.BRUSSELS_SPROUTS),
                    deal(EGGS, t("Eggs (except organic)", "Œufs (sauf bio)", "Eieren (behalve bio)"), "Columbus", t("4 or 6 eggs", "4 pièces ou 6 pièces", "4 stuks of 6 stuks"), delhaizeSecondAt("-50%"), 4.73, "/2", was = 6.30, card = true, until = "21/10", product = GenericProduct.EGGS),
                    deal(FROZEN, t("Fish & chips or fish & fun", "Fish & chips ou fish & fun", "Fish & chips of fish & fun"), "Iglo", t("240 g to 352 g", "De 240 g à 352 g", "Van 240 g tot 352 g"), free("1+1", fr = "gratis"), 5.19, "/2", was = 10.38, card = true, until = "21/10", product = GenericProduct.FISH_FINGERS),
                    deal(PAPER, t("Moltonel Style toilet paper", "Papier toilette Moltonel style", "Toiletpapier Moltonel style"), "Lotus", t("18 rolls", "18 rouleaux", "18 rollen"), free("9+9", fr = "gratis"), 6.99, "/promopack", card = true, until = "21/10", product = GenericProduct.TOILET_PAPER),
                    deal(FISH, t("Scottish smoked salmon", "Saumon fumé d’Écosse", "Schotse gerookte zalm"), "Delhaize", same("130 g"), delhaizeSecondAt("-50%"), 11.39, "/2", was = 15.18, card = true, until = "21/10", product = GenericProduct.SALMON),
                    deal(MEAT, t("Pork or farmhouse chipolata", "Chipolata de porc ou de campagne", "Varkens- of boerenchipolata"), "Delhaize", t("180 g to 960 g or variable weight", "De 180 g à 960 g ou poids variable", "Van 180 g tot 960 g of variabel gewicht"), SECOND_TRAY_HALF_PRICE, 4.04, "/2", was = 5.38, card = true, until = "21/10", product = GenericProduct.SAUSAGES),
                    deal(FROZEN, t("Frozen vegetables", "Légumes surgelés", "Diepgevroren groenten"), "Marie Thumas", null, free("1+1", fr = "gratis"), 4.49, "/2", was = 8.98, card = true, until = "21/10"),
                    deal(SOFT_DRINKS, t("Fruit juice", "Jus de fruits", "Vruchtensap"), "Tropicana", t("900 ml or 1.5 L", "900 ml ou 1,5 L", "900 ml of 1,5 L"), delhaizeSecondAt("-50%"), 5.99, "/2", was = 7.98, card = true, until = "21/10", product = GenericProduct.ORANGE_JUICE),
                    deal(CHEESE, t("Raclette or fondue cheese", "Fromage pour raclette ou fondue", "Kaas voor raclette of fondue"), "Delhaize", t("180 g to 550 g", "De 180 g à 550 g", "Van 180 g tot 550 g"), free("3+1", fr = "gratis"), 12.87, "/4", was = 17.16, card = true, until = "21/10", product = GenericProduct.CHEESE),
                    deal(POULTRY, t("Chipolata, burger, merguez, sausages, roll or strips", "Chipolata, burger, merguez, saucisses, roulade ou émincés", "Chipolata, burger, merguez, worsten, rollade of reepjes"), "Coq Ardenne", null, delhaizeSecondAt("-50%"), 15.74, "/kg", was = 20.99, card = true, until = "21/10"),
                    deal(PASTA, t("Ready-to-heat rice pouch", "Riz prêt à réchauffer en sachet", "Rijst om op te warmen in zakje"), "Ben’s Original", same("220 g"), free("2+2", fr = "gratis"), 4.70, "/4", was = 9.40, card = true, until = "21/10", product = GenericProduct.RICE),
                    deal(COFFEE_TEA, t("Coffee capsules", "Café en capsules", "Koffiecapsules"), "L’Or", same("10 capsules"), free("2+2", fr = "gratis"), 9.18, "/4", was = 18.36, card = true, until = "21/10", product = GenericProduct.COFFEE_CAPSULES),
                    deal(CANNED, t("Olive oil", "Huile d’olive", "Olijfolie"), "Filippo Berio", t("200 ml to 3 L", "De 200 ml à 3 L", "Van 200 ml tot 3 L"), delhaizeSecondAt("-50%"), 11.99, "/2", was = 15.98, card = true, until = "21/10", product = GenericProduct.OLIVE_OIL),
                    deal(CANNED, t("Tomato sauce", "Sauce tomate", "Tomatensaus"), "Mutti", t("130 g to 2 x 400 g", "De 130 g à 2 x 400 g", "Van 130 g tot 2 x 400 g"), delhaizeSecondAt("-50%"), 4.58, "/2", was = 6.10, card = true, until = "21/10", product = GenericProduct.CANNED_FOOD),
                    deal(SOFT_DRINKS, t("Zero sugar sparkling drink", "Boisson pétillante Zero sugar", "Bruisende drank Zero sugar"), "Pepsi", same("4 x 1 L"), free("1+1", fr = "gratis"), 6.99, "/2", was = 13.98, card = true, until = "21/10"),
                    deal(FROZEN, t("Snack & Share chicken snack", "Snack & Share - Snack au poulet", "Snack & Share - Snack met kip"), "Iglo", t("220 g to 375 g", "De 220 g à 375 g", "Van 220 g tot 375 g"), free("1+1", fr = "gratis"), 5.35, "/2", was = 10.70, card = true, until = "21/10"),
                    deal(DAIRY, t("Margarine or drinking yoghurt", "Margarine ou yaourt à boire", "Margarine of drinkyoghurt"), "Benecol", t("225 g to 8 x 67.5 g", "De 225 g à 8 x 67,5 g", "Van 225 g tot 8 x 67,5 g"), free("1+1", fr = "gratis"), 4.65, "/2", was = 9.30, card = true, until = "21/10", product = GenericProduct.BUTTER),
                    deal(WATER, t("Intense sparkling natural mineral water", "Eau minérale naturelle pétillante Intense", "Bruisend natuurlijk mineraalwater Intense"), "Spa", t("6 x 1.5 L", "6 x 1,5 L", "6 x 1,5 L"), delhaizeSecondAt("-40%"), 10.06, "/2", was = 12.58, card = true, until = "21/10", product = GenericProduct.WATER),
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
            // Written by tools/lidl_deals.py from lidl.be; run it again for a new week.
            "lidl" to deals("lidl", LIDL_WEEKLY_DEALS),
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
            // From promotiez.be on 4 October 2026: the folder valid 30/09 to 12/10 and the end of the
            // 23/09 to 05/10 one. The site gives no original prices.
            "carrefour" to deals(
                "carrefour",
                listOf(
                    deal(DAIRY, t("Greek-style yoghurt", "Spécialité laitière à la grecque", "Griekse zuivelspecialiteit"), "Danone", same("2 x (4 x 110 g)"), null, 2.95, until = "12/10", product = GenericProduct.YOGHURT),
                    deal(WATER, t("Natural mineral water", "Eau minérale naturelle", "Natuurlijk mineraalwater"), "Evian", t("4 x (6 x 1.5 L)", "4 x (6 x 1,5 L)", "4 x (6 x 1,5 L)"), null, 11.10, until = "12/10", product = GenericProduct.WATER),
                    deal(BABY, t("Baby-Dry pants and wipes", "Culottes Baby-Dry et lingettes", "Broekjes Baby-Dry en doekjes"), "Pampers", null, null, 7.18, until = "12/10"),
                    deal(PASTA, t("Extra virgin olive oil Classico", "Huile d’olive vierge extra Classico", "Extra zuivere olijfolie Classico"), "De Cecco", same("2 x 1 L"), null, 12.19, until = "05/10", product = GenericProduct.OLIVE_OIL),
                    deal(DAIRY, t("Soya drinks", "Boissons au soja", "Plantaardige dranken met soja"), "Alpro", same("2 x (6 x 250 ml)"), null, 5.45, until = "05/10"),
                    deal(COFFEE_TEA, t("Ground coffee Excellent", "Café moulu Excellent", "Gemalen koffie Excellent"), "Douwe Egberts", same("6 x 250 g"), null, 21.87, until = "05/10", product = GenericProduct.GROUND_COFFEE),
                ) + madeUp(14, except = setOf("Greek yoghurt", "Ground coffee")),
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
