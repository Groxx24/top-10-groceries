package com.top10.products.data.hardcoded

import com.top10.products.domain.model.Store
import com.top10.products.domain.repository.StoreRepository

/** The stores, until they are read from Firebase too. Their top lists are in Firestore. */
class HardcodedCatalog : StoreRepository {

    override suspend fun stores(): List<Store> = STORES

    private companion object {
        val DELHAIZE = Store(id = "delhaize", name = "Delhaize", logoUrl = commonsThumbnail("f/fe/Delhaize_wordmark.svg"))
        val ALDI = Store(id = "aldi", name = "Aldi", logoUrl = commonsThumbnail("2/2c/Aldi_Nord_201x_logo.svg"))
        val LIDL = Store(id = "lidl", name = "Lidl", logoUrl = commonsThumbnail("b/b2/Lidl_logo.svg"))
        val INTERMARCHE = Store(id = "intermarche", name = "Intermarché", logoUrl = commonsThumbnail("1/18/Intermarch%C3%A9_2009_logo.svg"))
        val SPAR = Store(id = "spar", name = "Spar", logoUrl = commonsThumbnail("7/7c/Spar-logo.svg"))

        val STORES = listOf(DELHAIZE, ALDI, LIDL, INTERMARCHE, SPAR)

        /**
         * A 330 px Wikimedia Commons thumbnail of the file at [path], e.g. "b/b2/Lidl_logo.svg".
         * Commons renders an SVG's thumbnail as a PNG, named after the SVG plus ".png".
         */
        private fun commonsThumbnail(path: String): String {
            val name = path.substringAfterLast('/')
            val extension = if (name.endsWith(".svg")) ".png" else ""
            return "https://upload.wikimedia.org/wikipedia/commons/thumb/$path/330px-$name$extension"
        }
    }
}
