package com.top10.groceries.data.delhaize

import com.top10.groceries.domain.model.ProductCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class DelhaizeCategoryMapperTest {

    private val mapper = DelhaizeCategoryMapper()

    @Test
    fun `fresh meat is meat`() {
        assertEquals(
            ProductCategory.MEAT_FISH,
            mapper.map("v2MEA", "/fr/shop/Viande-poisson-et-produits-vegetariens/Viande-fraiche/Boeuf/Entrecote/p/1"),
        )
    }

    @Test
    fun `vegetarian shelf under meat is told apart in every language`() {
        listOf(
            "/fr/shop/Viande-poisson-et-produits-vegetariens/Vegetarien/Falafel/p/1",
            "/nl/shop/Vlees-vis-en-vegetarische-producten/Vegetarisch/Falafel/p/1",
            "/shop/Meat-fish-and-veggie-products/Vegetarian/Falafel/p/1",
            "/fr/shop/Regime-specifique/Vegan/Falafel/p/1",
        ).forEach { assertEquals(it, ProductCategory.VEGETARIAN, mapper.map("v2MEA", it)) }
    }

    @Test
    fun `juice under fruit counts as a drink`() {
        assertEquals(
            ProductCategory.DRINKS,
            mapper.map("v2FRU", "/nl/shop/Verse-groenten-en-fruit/Verse-sappen-en-smoothies/x/p/1"),
        )
        assertEquals(
            ProductCategory.FRUIT_VEGETABLES,
            mapper.map("v2FRU", "/nl/shop/Verse-groenten-en-fruit/Verse-groenten/x/p/1"),
        )
    }

    @Test
    fun `pastry under bakery is not bread`() {
        assertEquals(ProductCategory.SWEETS_PASTRY, mapper.map("v2BAK", "/nl/shop/Bakkerij-en-banket/Patisserie/x/p/1"))
        assertEquals(ProductCategory.BREAD, mapper.map("v2BAK", "/nl/shop/Bakkerij-en-banket/Voorverpakt-brood/x/p/1"))
    }

    @Test
    fun `code is matched whatever its case`() {
        assertEquals(ProductCategory.ALCOHOL, mapper.map("V2ALC", null))
        assertEquals(ProductCategory.ALCOHOL, mapper.map("v2WIN", null))
    }

    @Test
    fun `missing or unknown category is other`() {
        assertEquals(ProductCategory.OTHER, mapper.map(null, "/fr/shop/c/Langes-Taille-1/p/1"))
        assertEquals(ProductCategory.OTHER, mapper.map("v2XYZ", null))
    }
}
