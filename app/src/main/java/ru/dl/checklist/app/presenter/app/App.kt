package ru.dl.checklist.app.presenter.app

import android.app.Application
import android.content.Context
import ru.dl.checklist.app.di.component.AppComponent
import ru.dl.checklist.app.di.component.DaggerAppComponent
import ru.dl.checklist.app.di.module.DataBaseModule
import timber.log.Timber

class App: Application() {
    init {
        instance = this
    }

    companion object {
        private var instance: App? = null
        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

        lateinit var appComponent: AppComponent
    }
    override fun onCreate() {
        super.onCreate()
        Timber.plant(LineNumberDebugTree())
        appComponent = DaggerAppComponent
            .builder()
            .databaseModule(DataBaseModule(this))
            .build()
    }
    inner class LineNumberDebugTree : Timber.DebugTree() {

        override fun createStackElementTag(element: StackTraceElement): String {
            return "CL(${element.fileName}:${element.lineNumber})"
        }

        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            super.log(priority, "$tag", message, t)
        }
    }
}