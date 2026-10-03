package com.top10.products.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

const val TopListDatabaseFileName = "top-lists.db"

/** The top lists last read from Firestore, kept on the phone so opening a store does not read them again. */
@Database(entities = [CachedTopListEntity::class, CachedOfferEntity::class], version = 1)
@ConstructedBy(TopListDatabaseConstructor::class)
abstract class TopListDatabase : RoomDatabase() {
    abstract fun topListDao(): TopListDao
}

// Room generates the actual for each platform.
@Suppress("KotlinNoActualForExpect")
expect object TopListDatabaseConstructor : RoomDatabaseConstructor<TopListDatabase> {
    override fun initialize(): TopListDatabase
}

/** Finishes a platform-specific builder with the settings every platform shares. */
fun RoomDatabase.Builder<TopListDatabase>.buildTopListDatabase(): TopListDatabase = this
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
    // Only a cache: rather than migrate it, a new schema starts empty and is read again.
    .fallbackToDestructiveMigration(dropAllTables = true)
    .build()
