package dev.xero.tomabar

import android.app.Application
import dev.xero.tomabar.di.AppContainer

class TomaBarApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}