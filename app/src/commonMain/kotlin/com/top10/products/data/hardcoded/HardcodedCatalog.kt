package com.top10.products.data.hardcoded

import com.top10.products.domain.model.Deal
import com.top10.products.domain.model.Offer
import com.top10.products.domain.model.Store
import com.top10.products.domain.model.TopOffers
import com.top10.products.domain.repository.StoreRepository
import com.top10.products.domain.repository.TopOffersRepository

/**
 * Made-up stores and top lists, until the master app publishes the real ones to Firebase.
 * Replacing this means writing Firebase versions of the two repositories and swapping them in
 * `AppContainer`; nothing above the data layer changes.
 */
class HardcodedCatalog : StoreRepository, TopOffersRepository {

    override suspend fun stores(): List<Store> = STORES

    override suspend fun topOffers(storeId: String): TopOffers {
        val store = STORES.firstOrNull { it.id == storeId } ?: error("Unknown store $storeId")
        val offers = TOP_OFFERS[storeId] ?: error("No top list for $storeId")
        return TopOffers(store = store, offers = offers)
    }

    private companion object {
        val DELHAIZE = Store(id = "delhaize", name = "Delhaize", logoUrl = commonsThumbnail("f/fe/Delhaize_wordmark.svg"))
        val ALDI = Store(id = "aldi", name = "Aldi", logoUrl = commonsThumbnail("2/2c/Aldi_Nord_201x_logo.svg"))
        val LIDL = Store(id = "lidl", name = "Lidl", logoUrl = commonsThumbnail("b/b2/Lidl_logo.svg"))
        val INTERMARCHE = Store(id = "intermarche", name = "Intermarché", logoUrl = commonsThumbnail("1/18/Intermarch%C3%A9_2009_logo.svg"))
        val SPAR = Store(id = "spar", name = "Spar", logoUrl = commonsThumbnail("7/7c/Spar-logo.svg"))

        val STORES = listOf(DELHAIZE, ALDI, LIDL, INTERMARCHE, SPAR)

        val TOP_OFFERS: Map<String, List<Offer>> = mapOf(
            DELHAIZE.id to listOf(
                offer(1, "Chicken breast fillets", "Delhaize", "Meat", "600 g", 8.49, "1+1 free", 0.5, 2, image = "b/b8/Raw_chicken_slices.jpg"),
                offer(2, "Atlantic salmon fillets", "Delhaize", "Fish", "250 g", 7.99, "-40%", 0.4, image = "2/24/Raw_salmon_fillets.jpg"),
                offer(3, "Sourdough loaf", null, "Bread", "800 g", 3.59, "2nd at -50%", 0.25, 2, image = "c/c6/Sourdough_Bread_Loaf.jpg"),
                offer(4, "Vine tomatoes", null, "Fruit & vegetables", "500 g", 2.79, "-35%", 0.35, image = "b/be/Grape_tomatoes_on_the_vine_at_Ljubljana_Central_Market.JPG"),
                offer(5, "Minced beef and pork", "Delhaize", "Meat", "1 kg", 9.29, "-30%", 0.3, card = true, image = "2/21/2018-04-22_Minced_beef_meat.jpg"),
                offer(6, "Jonagold apples", null, "Fruit & vegetables", "1.5 kg", 3.99, "1+1 free", 0.5, 2, image = "b/b4/Apfel-Jonagold.jpg"),
                offer(7, "Greek yoghurt", "Fage", "Dairy & eggs", "4 x 150 g", 4.49, "-33%", 0.33, image = "b/b8/Joghurt.jpg"),
                offer(8, "Free-range eggs", "Delhaize", "Dairy & eggs", "12 pieces", 4.19, "2+1 free", 0.33, 3, image = "4/4c/Egg_cartons_with_chicken_eggs_02.jpg"),
                offer(9, "Penne rigate", "Barilla", "Pantry", "1 kg", 2.89, "-40%", 0.4, image = "1/1d/Barilla_penne_1.jpg"),
                offer(10, "Lasagne bolognese", "Delhaize", "Ready meals", "1 kg", 7.49, "-30%", 0.3, image = "1/19/Lasagna_bolognese_al_forne%2C_june_2009.jpg"),
            ),
            ALDI.id to listOf(
                offer(1, "Minced beef and pork", "Aldi", "Meat", "1 kg", 7.99, "-40%", 0.4, image = "2/21/2018-04-22_Minced_beef_meat.jpg"),
                offer(2, "Free-range eggs", "Aldi", "Dairy & eggs", "10 pieces", 3.29, "1+1 free", 0.5, 2, image = "4/4c/Egg_cartons_with_chicken_eggs_02.jpg"),
                offer(3, "Jonagold apples", null, "Fruit & vegetables", "2 kg", 3.49, "-30%", 0.3, image = "b/b4/Apfel-Jonagold.jpg"),
                offer(4, "Penne rigate", "Aldi", "Pantry", "500 g", 0.99, "2+1 free", 0.33, 3, image = "1/1d/Barilla_penne_1.jpg"),
                offer(5, "Chicken breast fillets", "Aldi", "Meat", "1 kg", 9.99, "-25%", 0.25, image = "b/b8/Raw_chicken_slices.jpg"),
                offer(6, "Atlantic salmon fillets", "Aldi", "Fish", "2 x 125 g", 5.49, "-30%", 0.3, image = "2/24/Raw_salmon_fillets.jpg"),
                offer(7, "Vine tomatoes", null, "Fruit & vegetables", "1 kg", 2.99, "-35%", 0.35, image = "b/be/Grape_tomatoes_on_the_vine_at_Ljubljana_Central_Market.JPG"),
                offer(8, "Greek yoghurt", "Aldi", "Dairy & eggs", "1 kg", 2.79, "-20%", 0.2, image = "b/b8/Joghurt.jpg"),
                offer(9, "Lasagne bolognese", "Aldi", "Ready meals", "400 g", 2.69, "2nd at -50%", 0.25, 2, image = "1/19/Lasagna_bolognese_al_forne%2C_june_2009.jpg"),
                offer(10, "Sourdough loaf", null, "Bread", "500 g", 2.19, "-20%", 0.2, image = "c/c6/Sourdough_Bread_Loaf.jpg"),
            ),
            LIDL.id to listOf(
                offer(1, "Atlantic salmon fillets", "Lidl", "Fish", "400 g", 8.99, "1+1 free", 0.5, 2, card = true, image = "2/24/Raw_salmon_fillets.jpg"),
                offer(2, "Chicken breast fillets", "Lidl", "Meat", "650 g", 6.99, "-45%", 0.45, image = "b/b8/Raw_chicken_slices.jpg"),
                offer(3, "Greek yoghurt", "Lidl", "Dairy & eggs", "500 g", 1.89, "2+1 free", 0.33, 3, image = "b/b8/Joghurt.jpg"),
                offer(4, "Sourdough loaf", null, "Bread", "750 g", 2.99, "-30%", 0.3, image = "c/c6/Sourdough_Bread_Loaf.jpg"),
                offer(5, "Vine tomatoes", null, "Fruit & vegetables", "500 g", 1.99, "1+1 free", 0.5, 2, image = "b/be/Grape_tomatoes_on_the_vine_at_Ljubljana_Central_Market.JPG"),
                offer(6, "Penne rigate", "Barilla", "Pantry", "1 kg", 2.79, "-35%", 0.35, image = "1/1d/Barilla_penne_1.jpg"),
                offer(7, "Minced beef and pork", "Lidl", "Meat", "500 g", 4.49, "-30%", 0.3, card = true, image = "2/21/2018-04-22_Minced_beef_meat.jpg"),
                offer(8, "Jonagold apples", null, "Fruit & vegetables", "1 kg", 2.49, "-25%", 0.25, image = "b/b4/Apfel-Jonagold.jpg"),
                offer(9, "Lasagne bolognese", "Lidl", "Ready meals", "1 kg", 5.99, "-25%", 0.25, image = "1/19/Lasagna_bolognese_al_forne%2C_june_2009.jpg"),
                offer(10, "Free-range eggs", "Lidl", "Dairy & eggs", "6 pieces", 1.99, "-20%", 0.2, image = "4/4c/Egg_cartons_with_chicken_eggs_02.jpg"),
            ),
            INTERMARCHE.id to listOf(
                offer(1, "Lasagne bolognese", "Intermarché", "Ready meals", "1 kg", 6.99, "1+1 free", 0.5, 2, image = "1/19/Lasagna_bolognese_al_forne%2C_june_2009.jpg"),
                offer(2, "Vine tomatoes", null, "Fruit & vegetables", "1 kg", 3.49, "-40%", 0.4, image = "b/be/Grape_tomatoes_on_the_vine_at_Ljubljana_Central_Market.JPG"),
                offer(3, "Chicken breast fillets", "Intermarché", "Meat", "500 g", 6.79, "2nd at -50%", 0.25, 2, image = "b/b8/Raw_chicken_slices.jpg"),
                offer(4, "Greek yoghurt", "Fage", "Dairy & eggs", "4 x 150 g", 4.59, "-35%", 0.35, card = true, image = "b/b8/Joghurt.jpg"),
                offer(5, "Atlantic salmon fillets", "Intermarché", "Fish", "300 g", 8.49, "-30%", 0.3, image = "2/24/Raw_salmon_fillets.jpg"),
                offer(6, "Sourdough loaf", null, "Bread", "800 g", 3.79, "-30%", 0.3, image = "c/c6/Sourdough_Bread_Loaf.jpg"),
                offer(7, "Penne rigate", "Barilla", "Pantry", "500 g", 1.69, "2+1 free", 0.33, 3, image = "1/1d/Barilla_penne_1.jpg"),
                offer(8, "Free-range eggs", "Intermarché", "Dairy & eggs", "12 pieces", 4.29, "-25%", 0.25, image = "4/4c/Egg_cartons_with_chicken_eggs_02.jpg"),
                offer(9, "Minced beef and pork", "Intermarché", "Meat", "750 g", 7.29, "-25%", 0.25, image = "2/21/2018-04-22_Minced_beef_meat.jpg"),
                offer(10, "Jonagold apples", null, "Fruit & vegetables", "1.5 kg", 3.89, "-20%", 0.2, image = "b/b4/Apfel-Jonagold.jpg"),
            ),
            SPAR.id to listOf(
                offer(1, "Greek yoghurt", "Fage", "Dairy & eggs", "1 kg", 6.49, "1+1 free", 0.5, 2, image = "b/b8/Joghurt.jpg"),
                offer(2, "Sourdough loaf", null, "Bread", "600 g", 3.29, "-40%", 0.4, image = "c/c6/Sourdough_Bread_Loaf.jpg"),
                offer(3, "Free-range eggs", "Spar", "Dairy & eggs", "12 pieces", 4.39, "-35%", 0.35, image = "4/4c/Egg_cartons_with_chicken_eggs_02.jpg"),
                offer(4, "Atlantic salmon fillets", "Spar", "Fish", "250 g", 8.29, "2nd at -50%", 0.25, 2, image = "2/24/Raw_salmon_fillets.jpg"),
                offer(5, "Jonagold apples", null, "Fruit & vegetables", "1 kg", 2.99, "2+1 free", 0.33, 3, image = "b/b4/Apfel-Jonagold.jpg"),
                offer(6, "Chicken breast fillets", "Spar", "Meat", "400 g", 6.49, "-30%", 0.3, card = true, image = "b/b8/Raw_chicken_slices.jpg"),
                offer(7, "Penne rigate", "Barilla", "Pantry", "500 g", 1.79, "-30%", 0.3, image = "1/1d/Barilla_penne_1.jpg"),
                offer(8, "Lasagne bolognese", "Spar", "Ready meals", "600 g", 5.49, "-25%", 0.25, image = "1/19/Lasagna_bolognese_al_forne%2C_june_2009.jpg"),
                offer(9, "Vine tomatoes", null, "Fruit & vegetables", "500 g", 2.89, "-25%", 0.25, image = "b/be/Grape_tomatoes_on_the_vine_at_Ljubljana_Central_Market.JPG"),
                offer(10, "Minced beef and pork", "Spar", "Meat", "500 g", 5.29, "-20%", 0.2, image = "2/21/2018-04-22_Minced_beef_meat.jpg"),
            ),
        )

        private fun offer(
            rank: Int,
            name: String,
            brand: String?,
            category: String,
            packageSize: String,
            price: Double,
            dealLabel: String,
            discount: Double,
            requiredQuantity: Int = 1,
            card: Boolean = false,
            image: String? = null,
        ) = Offer(
            rank = rank,
            productId = "p$rank",
            brand = brand,
            name = name,
            category = category,
            packageSize = packageSize,
            imageUrl = image?.let { commonsThumbnail(it) },
            productUrl = null,
            price = price,
            deal = Deal(
                label = dealLabel,
                discount = discount,
                requiredQuantity = requiredQuantity,
                needsLoyaltyCard = card,
                validUntil = "14/10",
            ),
        )

        /**
         * A 330 px Wikimedia Commons thumbnail of the file at [path], e.g. "b/b4/Apfel-Jonagold.jpg".
         * Commons renders an SVG's thumbnail as a PNG, named after the SVG plus ".png".
         */
        private fun commonsThumbnail(path: String): String {
            val name = path.substringAfterLast('/')
            val extension = if (name.endsWith(".svg")) ".png" else ""
            return "https://upload.wikimedia.org/wikipedia/commons/thumb/$path/330px-$name$extension"
        }
    }
}
