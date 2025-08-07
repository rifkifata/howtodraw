package com.htd.cars

import android.content.Context
import android.content.SharedPreferences

object ThemeHelper {
    private const val PREF_NAME = "theme_pref"
    private const val KEY_THEME = "selected_theme"
    
    const val THEME_BOY = "boy"
    const val THEME_GIRL = "girl"
    
    /**
     * Get current theme from SharedPreferences
     */
    fun getCurrentTheme(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val theme = prefs.getString(KEY_THEME, THEME_GIRL) ?: THEME_GIRL // Default to girl theme
        android.util.Log.d("ThemeHelper", "Current theme retrieved: $theme")
        return theme
    }
    
    /**
     * Save selected theme to SharedPreferences
     */
    fun setTheme(context: Context, theme: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_THEME, theme).apply()
        android.util.Log.d("ThemeHelper", "Theme saved: $theme")
    }
    
    /**
     * Apply theme to the app
     */
    fun applyTheme(context: Context, theme: String) {
        setTheme(context, theme)
        updateTheme(context, theme)
    }
    
    /**
     * Update app theme
     */
    private fun updateTheme(context: Context, theme: String) {
        // This will be called when theme changes
        // For now, we'll just store the preference
        // In a real app, you might want to recreate the activity or update colors dynamically
    }
    
    /**
     * Get theme colors based on selected theme
     */
    fun getThemeColors(theme: String): ThemeColors {
        return when (theme) {
            THEME_GIRL -> ThemeColors(
                primaryColor = "#FF1493",      // Deep Pink
                secondaryColor = "#DA70D6",    // Orchid
                backgroundColor = "#FFF0F5",   // Lavender Blush
                accentColor = "#FFB6C1",       // Light Pink
                textColor = "#8B008B",         // Dark Magenta
                cardBackgroundColor = "#FFE4E1", // Misty Rose
                cardTextColor = "#C71585",     // Medium Violet Red
                headerBackgroundColor = "#FF69B4", // Hot Pink
                buttonColor = "#FF1493"        // Deep Pink
            )
            else -> ThemeColors(
                primaryColor = "#0066CC",      // Deep Blue
                secondaryColor = "#00CC66",    // Bright Green
                backgroundColor = "#E6F3FF",   // Light Blue
                accentColor = "#66CCFF",       // Sky Blue
                textColor = "#003366",         // Dark Blue
                cardBackgroundColor = "#E6F7FF", // Light Cyan
                cardTextColor = "#0066CC",     // Deep Blue
                headerBackgroundColor = "#0066CC", // Deep Blue
                buttonColor = "#00CC66"        // Bright Green
            )
        }
    }
    
    /**
     * Get current theme colors
     */
    fun getCurrentThemeColors(context: Context): ThemeColors {
        val currentTheme = getCurrentTheme(context)
        return getThemeColors(currentTheme)
    }
}

data class ThemeColors(
    val primaryColor: String,
    val secondaryColor: String,
    val backgroundColor: String,
    val accentColor: String,
    val textColor: String,
    val cardBackgroundColor: String,
    val cardTextColor: String,
    val headerBackgroundColor: String,
    val buttonColor: String
)
