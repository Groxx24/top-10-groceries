package com.top10.products

import android.app.Application
import com.top10.products.di.AppContainer

class Top10ProductsApplication : Application() {
    /** Lives as long as the process, so it outlives any single activity. */
    val container: AppContainer by lazy { AppContainer() }
}
