package com.gsrikar.videocatalogue.app

import android.app.Application
import android.content.Context

class VideoApplication: Application() {

    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}