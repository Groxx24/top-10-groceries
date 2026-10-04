package com.top10.deals.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface TopListDao {
    /** When the store's list was last read and when it ends, or null if it was never read. */
    @Query("SELECT * FROM top_lists WHERE storeId = :storeId")
    suspend fun list(storeId: String): CachedTopListEntity?

    /** The store's cached offers, best first. */
    @Query("SELECT * FROM top_offers WHERE storeId = :storeId ORDER BY rank")
    suspend fun offers(storeId: String): List<CachedOfferEntity>

    /** Swaps the store's cached list for [list] and its [offers], all at once. */
    @Transaction
    suspend fun replace(list: CachedTopListEntity, offers: List<CachedOfferEntity>) {
        deleteOffers(list.storeId)
        insertOffers(offers)
        upsertList(list)
    }

    /** Forgets the store's cached list, so it is read again. */
    @Transaction
    suspend fun delete(storeId: String) {
        deleteOffers(storeId)
        deleteList(storeId)
    }

    @Query("DELETE FROM top_offers WHERE storeId = :storeId")
    suspend fun deleteOffers(storeId: String)

    @Insert
    suspend fun insertOffers(offers: List<CachedOfferEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertList(list: CachedTopListEntity)

    @Query("DELETE FROM top_lists WHERE storeId = :storeId")
    suspend fun deleteList(storeId: String)
}
