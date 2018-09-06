package com.hokm.personal.my.hokm

import android.os.Bundle
import android.preference.PreferenceFragment


class IconPickerFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.preferences)

    }

}