package com.top10.deals.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun topListDatabaseBuilder(context: Context): RoomDatabase.Builder<TopListDatabase> {
    val appContext = context.applicationContext
    return Room.databaseBuilder<TopListDatabase>(
        context = appContext,
        name = appContext.getDatabasePath(TopListDatabaseFileName).absolutePath,
    )
}
