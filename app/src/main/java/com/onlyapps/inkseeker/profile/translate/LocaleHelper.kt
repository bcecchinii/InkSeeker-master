package com.onlyapps.inkseeker.profile.translate

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.preference.PreferenceManager
import java.util.Locale

object LocaleHelper {

    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"

    fun setLocale(context: Context, language: String) {
        persist(context, language)
        return updateResources(context, language)

    }
    @Suppress("deprecation")
    fun getLocale(context: Context): Locale{
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val language = preferences.getString(SELECTED_LANGUAGE, "it") ?: "it"
        return Locale(language)
    }

    @Suppress("deprecation")
    private fun persist(context: Context,language: String) {
        val preferences : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().putString(SELECTED_LANGUAGE, language).apply()
    }

    @Suppress("deprecation")
    private fun updateResources(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        configuration.locale = locale

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale)
        }

        resources.updateConfiguration(configuration, resources.displayMetrics)
    }


}