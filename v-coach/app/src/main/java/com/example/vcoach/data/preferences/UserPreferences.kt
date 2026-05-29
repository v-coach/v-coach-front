package com.example.vcoach.data.preferences

import android.content.Context
import com.example.vcoach.core.constants.PreferenceKeys.DEFAULT_USER_TYPE
import com.example.vcoach.core.constants.PreferenceKeys.PREFERENCE_NAME
import com.example.vcoach.core.constants.PreferenceKeys.USER_TYPE_KEY

class UserPreferences(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCE_NAME,
        Context.MODE_PRIVATE,
    )

    fun getUserType(): String {
        return preferences.getString(USER_TYPE_KEY, DEFAULT_USER_TYPE) ?: DEFAULT_USER_TYPE
    }

    fun saveUserType(userType: String) {
        preferences.edit()
            .putString(USER_TYPE_KEY, userType)
            .apply()
    }
}
