package com.top10.groceries.data.hardcoded

import com.top10.groceries.domain.model.Deal
import com.top10.groceries.domain.model.Offer
import com.top10.groceries.domain.model.Store
import com.top10.groceries.domain.model.TopOffers
import com.top10.groceries.domain.repository.StoreRepository
import com.top10.groceries.domain.repository.TopOffersRepository

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
        val DELHAIZE = Store(id = "delhaize", name = "Delhaize")

        val STORES = listOf(DELHAIZE)

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

        /** A 330 px Wikimedia Commons thumbnail of the file at [path], e.g. "b/b4/Apfel-Jonagold.jpg". */
        private fun commonsThumbnail(path: String): String =
            "https://upload.wikimedia.org/wikipedia/commons/thumb/$path/330px-${path.substringAfterLast('/')}"
    }
}
