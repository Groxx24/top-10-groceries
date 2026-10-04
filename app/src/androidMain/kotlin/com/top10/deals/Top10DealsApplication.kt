package com.top10.deals

import android.app.Application
import com.top10.deals.data.local.topListDatabaseBuilder
import com.top10.deals.di.AppContainer

class Top10DealsApplication : Application() {
    /** Lives as long as the process, so it outlives any single activity. */
    val container: AppContainer by lazy { AppContainer(topListDatabaseBuilder(this)) }
}
