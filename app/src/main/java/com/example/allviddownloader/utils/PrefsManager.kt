package com.example.allviddownloader.utils

import android.content.Context

class PrefsManager private constructor(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    companion object {
        private const val PREFERENCES = "VIDAPP_PREFS"
        open var COOKIES = "Cookies"
        open var CSRF = "csrf"
        open var SESSIONID = "session_id"
        open var USERID = "user_id"
        open var ISINSTALOGIN = "IsInstaLogin"

        @Synchronized
        fun newInstance(context: Context) = PrefsManager(context)
    }

    fun putBoolean(key: String, value: Boolean) = preferences.edit().putBoolean(key, value).apply()

    fun getBoolean(key: String, defValue: Boolean) = preferences.getBoolean(key, defValue)

    fun putInt(key: String, value: Int) = preferences.edit().putInt(key, value).apply()

    fun getInt(key: String, defValue: Int) = preferences.getInt(key, defValue)

    fun putFloat(key: String, value: Float) = preferences.edit().putFloat(key, value).apply()

    fun getFloat(key: String, defValue: Float) = preferences.getFloat(key, defValue)

    fun putLong(key: String, value: Long) = preferences.edit().putLong(key, value).apply()

    fun getLong(key: String, defValue: Long) = preferences.getLong(key, defValue)

    fun putString(key: String, value: String) = preferences.edit().putString(key, value).apply()

    fun getString(key: String, defValue: String) = preferences.getString(key, defValue)
}
