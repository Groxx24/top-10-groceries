package com.top10.groceries

import android.app.Application
import com.top10.groceries.di.AppContainer
import java.util.Locale

class Top10GroceriesApplication : Application() {
    /** Lives as long as the process, so it outlives any single activity. */
    val container: AppContainer by lazy {
        AppContainer(deviceLanguage = Locale.getDefault().language)
    }
}
