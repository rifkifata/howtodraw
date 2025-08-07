package com.htd.cars

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import java.util.*

object LanguageHelper {
    private const val PREF_NAME = "language_pref"
    private const val KEY_LANGUAGE = "selected_language"
    private const val KEY_FOLLOW_SYSTEM = "follow_system_language"
    
    // Language codes mapping
    private val languageMap = linkedMapOf(
        "English" to "en",
        "Indonesia" to "id",
        "\u4e2d\u6587" to "zh",
        "\u0939\u093f\u0928\u094d\u0926\u0940" to "hi"
    )
    
    /**
     * Get system default language code
     */
    fun getSystemLanguageCode(): String {
        val systemLocale = Locale.getDefault()
        val systemLanguage = systemLocale.language
        val systemCountry = systemLocale.country
        
        // Try to match exact language-country combination first
        val fullLocale = "$systemLanguage-$systemCountry"
        if (languageMap.values.contains(fullLocale)) {
            return fullLocale
        }
        
        // Try to match just language code
        if (languageMap.values.contains(systemLanguage)) {
            return systemLanguage
        }
        
        // If system language not supported, return English
        return "en"
    }
    
    /**
     * Check if app should follow system language
     */
    fun shouldFollowSystemLanguage(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_FOLLOW_SYSTEM, true) // Default to true
    }
    
    /**
     * Set whether app should follow system language
     */
    fun setFollowSystemLanguage(context: Context, follow: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_FOLLOW_SYSTEM, follow).apply()
    }
    
    /**
     * Get current language from SharedPreferences or system default
     * Now prioritizes system language if follow_system is enabled
     */
    fun getCurrentLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val savedLanguage = prefs.getString(KEY_LANGUAGE, null)
        return if (savedLanguage == null) {
            val systemLanguage = getSystemLanguageCode()
            prefs.edit().putString(KEY_LANGUAGE, systemLanguage).apply()
            systemLanguage
        } else {
            if (languageMap.values.contains(savedLanguage)) {
                savedLanguage
            } else {
                val systemLanguage = getSystemLanguageCode()
                prefs.edit().putString(KEY_LANGUAGE, systemLanguage).apply()
                systemLanguage
            }
        }
    }
    
    /**
     * Check if system language has changed and update if needed
     */
    fun checkAndUpdateSystemLanguage(context: Context) {
        if (shouldFollowSystemLanguage(context)) {
            val currentLanguage = getCurrentLanguage(context)
            val systemLanguage = getSystemLanguageCode()
            
            if (currentLanguage != systemLanguage) {
                // System language has changed, update the app
                val systemLanguageName = getLanguageName(systemLanguage)
                applyLanguage(context, systemLanguageName)
            }
        }
    }
    
    /**
     * Save selected language to SharedPreferences
     */
    fun setLanguage(context: Context, language: String) {
        // Force mapping "in" to "id" for Indonesia
        val normalized = if (language == "in") "id" else language
        val languageCode = if (languageMap.values.contains(normalized)) normalized else languageMap[language] ?: getSystemLanguageCode()
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
        android.util.Log.d("LanguageHelper", "setLanguage: language input=$language, languageCode disimpan=$languageCode")
    }
    
    /**
     * Apply language to the app
     */
    fun applyLanguage(context: Context, language: String) {
        // Force mapping "in" to "id" for Indonesia
        val normalized = if (language == "in") "id" else language
        val languageCode = if (languageMap.values.contains(normalized)) normalized else languageMap[language] ?: getSystemLanguageCode()
        setLanguage(context, languageCode)
        updateResources(context, languageCode)
        android.util.Log.d("LanguageHelper", "applyLanguage: language input=$language, languageCode diterapkan=$languageCode")
    }
    
    /**
     * Update app resources with new language
     */
    private fun updateResources(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }
        
        context.createConfigurationContext(config)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
    
    /**
     * Get language name from code
     */
    fun getLanguageName(languageCode: String): String {
        return languageMap.entries.find { it.value == languageCode }?.key ?: "English"
    }
    
    /**
     * Get all available languages
     */
    fun getAvailableLanguages(): Array<String> {
        return languageMap.keys.toTypedArray()
    }
    
    /**
     * Get current language name
     */
    fun getCurrentLanguageName(context: Context): String {
        val languageCode = getCurrentLanguage(context)
        return getLanguageName(languageCode)
    }
    
    /**
     * Check if it's the first time app is launched
     */
    fun isFirstLaunch(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return !prefs.contains(KEY_LANGUAGE)
    }
    
    /**
     * Check if a language code is supported
     */
    fun isLanguageSupported(languageCode: String): Boolean {
        return languageMap.values.contains(languageCode)
    }
    
    /**
     * Get list of supported language codes
     */
    fun getSupportedLanguageCodes(): List<String> {
        return languageMap.values.toList()
    }
    
    /**
     * Reset language to system default
     */
    fun resetToSystemLanguage(context: Context) {
        val systemLanguage = getSystemLanguageCode()
        val systemLanguageName = getLanguageName(systemLanguage)
        setLanguage(context, systemLanguageName)
        applyLanguage(context, systemLanguageName)
        // Enable follow system language
        setFollowSystemLanguage(context, true)
    }
    
    /**
     * Get system language name
     */
    fun getSystemLanguageName(): String {
        val systemLanguage = getSystemLanguageCode()
        return getLanguageName(systemLanguage)
    }
    
    /**
     * Create a new context with updated language
     */
    fun createContextWithLanguage(context: Context): Context {
        val languageCode = getCurrentLanguage(context)
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }
        
        return context.createConfigurationContext(config)
    }
}
