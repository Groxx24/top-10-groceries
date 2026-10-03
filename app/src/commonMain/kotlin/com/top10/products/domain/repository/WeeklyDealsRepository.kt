package com.top10.products.domain.repository

import com.top10.products.domain.model.WeeklyDeal

interface WeeklyDealsRepository {
    /** This week's deals at the store with [storeId], most relevant first. */
    suspend fun weeklyDeals(storeId: String): List<WeeklyDeal>
}
