package com.hokm.personal.my.hokm

import com.hokm.personal.my.hokm.util.PrefsHelper

class HokmApp: android.app.Application(){
    override fun onCreate() {
        super.onCreate()
        PrefsHelper.init(this)
    }
}