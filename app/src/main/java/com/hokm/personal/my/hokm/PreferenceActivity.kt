package com.hokm.personal.my.hokm

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class PreferenceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val iconPickerFragment = IconPickerFragment()

        val fm = fragmentManager
        val ft = fm.beginTransaction()
        ft.replace(android.R.id.content, iconPickerFragment)
        ft.commit()
    }
}
