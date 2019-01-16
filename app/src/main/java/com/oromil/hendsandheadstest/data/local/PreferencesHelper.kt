package com.oromil.hendsandheadstest.data.local

import android.content.Context
import javax.inject.Inject

private const val USER_EMAIL = ""
private const val USER_NAME = ""

class PreferencesHelper @Inject constructor(context: Context) {
    private val preferences = context.getSharedPreferences("com.oromil.hendsandheadstest", Context.MODE_PRIVATE)

    fun saveUserEmail(email: String) {
        preferences.edit().putString(USER_EMAIL, email).apply()
    }

    fun saveUserName(name: String) {
        preferences.edit().putString(USER_NAME, name).apply()
    }

    fun getUserEmail() = preferences.getString(USER_EMAIL, "")

    fun getUserName() = preferences.getString(USER_NAME, "")
}