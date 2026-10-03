package com.top10.products

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.top10.products.ui.App

class MainActivity : ComponentActivity() {
    private val container get() = (application as Top10ProductsApplication).container

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            App(container)
        }
    }
}
