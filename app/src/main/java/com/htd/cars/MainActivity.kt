package com.htd.cars

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import java.io.IOException
import java.util.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultLauncher
import com.htd.cars.TutorialActivity

class MainActivity : BaseActivity() {
    private lateinit var carRecyclerView: RecyclerView
    private lateinit var carAdapter: CarMenuAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: LinearLayout

    companion object {
        private const val TAG = "MainActivity"

        // ScaleType options for different image display preferences
        private val SCALE_TYPE_OPTIONS = mapOf(
            "FIT_CENTER" to ImageView.ScaleType.FIT_CENTER,      // Shows entire image, may have empty space
            "CENTER_INSIDE" to ImageView.ScaleType.CENTER_INSIDE, // Similar to FIT_CENTER but won't scale up
            "CENTER_CROP" to ImageView.ScaleType.CENTER_CROP,    // Fills entire space but may crop image
            "CENTER" to ImageView.ScaleType.CENTER               // Centers image without scaling
        )
    }
    
    // Color mapping untuk tutorial menggambar mobil
    private val colorMap = mapOf(
        "Car" to Color.parseColor("#FFE3F2FD"),
        "Car Front View" to Color.parseColor("#FFF8BBD0"),
        "Firetruck" to Color.parseColor("#FFFFCDD2"),
        "Lamborghini" to Color.parseColor("#FFFFF9C4"),
        "Monster Truck" to Color.parseColor("#FFD1C4E9"),
        "Police Car" to Color.parseColor("#FFB3E5FC"),
        "Race car" to Color.parseColor("#FFC8E6C9"),
        "School Bus" to Color.parseColor("#FFFFF59D"),
        "Tractor" to Color.parseColor("#FFFFE0B2"),
        "Truck" to Color.parseColor("#FFD7CCC8")
    )

    // Text color mapping untuk tutorial menggambar mobil
    private val textColorMap = mapOf(
        "Car" to Color.parseColor("#FF1976D2"),
        "Car Front View" to Color.parseColor("#FFD81B60"),
        "Firetruck" to Color.parseColor("#FFD32F2F"),
        "Lamborghini" to Color.parseColor("#FFFBC02D"),
        "Monster Truck" to Color.parseColor("#FF7B1FA2"),
        "Police Car" to Color.parseColor("#FF0288D1"),
        "Race car" to Color.parseColor("#FF388E3C"),
        "School Bus" to Color.parseColor("#FFF9A825"),
        "Tractor" to Color.parseColor("#FFF57C00"),
        "Truck" to Color.parseColor("#FF5D4037")
    )

    // Current scale type setting
    private var currentScaleType = "FIT_CENTER"
    
    private var lastTutorialPosition: Int? = null
    private val tutorialLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val lastPosition = result.data?.getIntExtra("last_viewpager_position", -1) ?: -1
            if (lastPosition >= 0) {
                lastTutorialPosition = lastPosition
                Log.d(TAG, "Kembali dari TutorialActivity, posisi terakhir ViewPager2: $lastPosition")
                // Scroll ke posisi terakhir (index grid sama dengan step tutorial)
                carRecyclerView.post {
                    carRecyclerView.scrollToPosition(lastPosition)
                }
            }
        }
    }
    
    /**
     * Change the scale type for all images in the grid
     * Options: "FIT_CENTER", "CENTER_INSIDE", "CENTER_CROP", "CENTER"
     */
    fun setImageScaleType(scaleTypeName: String) {
        if (SCALE_TYPE_OPTIONS.containsKey(scaleTypeName)) {
            currentScaleType = scaleTypeName
            Log.d(TAG, "Scale type changed to: $scaleTypeName")
            // Reload menu to apply new scale type
            loadDynamicMenu()
        } else {
            Log.w(TAG, "Invalid scale type: $scaleTypeName. Available options: ${SCALE_TYPE_OPTIONS.keys.joinToString()}")
        }
    }
    
    /**
     * Get current scale type
     */
    fun getCurrentScaleType(): String {
        return currentScaleType
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate MainActivity, current lang: ${LanguageHelper.getCurrentLanguage(this)}")
        super.onCreate(savedInstanceState)
        
        // Force portrait orientation
        requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        
        // Apply system language automatically
        applySystemLanguage()
        
        setContentView(R.layout.activity_main)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Initialize views
        carRecyclerView = findViewById(R.id.carRecyclerView)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        // Setup RecyclerView
        setupRecyclerView()

        // Setup language settings button
        setupLanguageButton()

        // Setup theme settings button
        setupThemeButton()

        // Setup burger menu button
        setupBurgerButton()

        // Setup navigation drawer (LinearLayout version)
        setupNavigationDrawer()

        // Load car background image (last image from tutorial)
        loadCarBackground()

        // Apply current theme
        applyCurrentTheme()
        
        // Load dynamic menu
        loadDynamicMenu()
        

        

        
        // Test image loading
        testImageLoading()
        
        // Update language button flag (must be last)
        updateLanguageButtonFlag()
        
        // Update drawer title
        updateDrawerTitle()
        
        // Update subtitle
        updateSubtitle()
        
        // Update main title
        updateMainTitle()
        
        applyDrawerEmojiStyle()
    }
    
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        
        // Check if system language has changed
        if (LanguageHelper.shouldFollowSystemLanguage(this)) {
            LanguageHelper.checkAndUpdateSystemLanguage(this)
            Log.d(TAG, "Configuration changed - checking system language")
        }
    }
    
    private fun setupLanguageButton() {
        findViewById<TextView>(R.id.languageButton).setOnClickListener {
            showLanguageDialog()
        }
    }
    
    private fun setupThemeButton() {
        findViewById<TextView>(R.id.themeButton).setOnClickListener {
            showThemeDialog()
        }
    }
    
    private fun setupBurgerButton() {
        findViewById<TextView>(R.id.burgerButton).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }
    
    private fun setupNavigationDrawer() {
        findViewById<TextView>(R.id.nav_home).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            Log.d(TAG, "[DRAWER] nav_home clicked, closing drawer")
        }
        findViewById<TextView>(R.id.nav_about).setOnClickListener {
            showAboutDialog()
            Log.d(TAG, "[DRAWER] nav_about clicked, showing about dialog")
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        findViewById<TextView>(R.id.nav_help).setOnClickListener {
            showHelpDialog()
            Log.d(TAG, "[DRAWER] nav_help clicked, showing help dialog")
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        findViewById<TextView>(R.id.nav_language).setOnClickListener {
            showLanguageDialog()
            Log.d(TAG, "[DRAWER] nav_language clicked, showing language dialog")
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        findViewById<TextView>(R.id.nav_theme).setOnClickListener {
            showThemeDialog()
            Log.d(TAG, "[DRAWER] nav_theme clicked, showing theme dialog")
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }
    
    private fun showBurgerMenu() {
        val options = arrayOf("4da About", "699e0f Settings", "139e0f Help")
        
        AlertDialog.Builder(this)
            .setTitle("630 Menu")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showAboutDialog()
                    1 -> showSettingsDialog()
                    2 -> showHelpDialog()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle("4da About")
            .setMessage("Flower Drawing Tutorial App\n\nLearn to draw beautiful flowers step by step!\n\nVersion 1.0")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    private fun showSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("699e0f Settings")
            .setMessage("Settings options:\n\n022 Language: Change app language\n022 Theme: Choose boy or girl theme")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    private fun showHelpDialog() {
        AlertDialog.Builder(this)
            .setTitle("139e0f Help")
            .setMessage("How to use this app:\n\n1. Choose a flower category\n2. Follow the step-by-step tutorial\n3. Draw along with the images\n4. Have fun creating beautiful flowers!")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    private fun showLanguageDialog() {
        val languages = LanguageHelper.getAvailableLanguages()
        val languageCodes = LanguageHelper.getSupportedLanguageCodes().toTypedArray()
        val currentLanguageCode = LanguageHelper.getCurrentLanguage(this)
        val systemLanguage = LanguageHelper.getSystemLanguageName()
        
        // Create custom dialog with child-friendly design
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_language_selection)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        // Set dialog size
        val window = dialog.window
        window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        
        // Set title with system language info
        val titleText = dialog.findViewById<TextView>(R.id.dialogTitle)
        titleText.text = getString(R.string.select_language)
        
        // Setup RecyclerView for language list
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.languageRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val languageAdapter = LanguageAdapter(languages, languageCodes, currentLanguageCode) { selectedLanguageCode ->
            changeLanguage(selectedLanguageCode)
            dialog.dismiss()
        }
        recyclerView.adapter = languageAdapter
        
        // Setup cancel button
        val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        
        // Log current and system language for debugging
        Log.d(TAG, "Current language: $currentLanguageCode")
        Log.d(TAG, "System language: $systemLanguage")
        
        // Set dialog background sama seperti dialog tema
        val themeColors = ThemeHelper.getCurrentThemeColors(this)
        val rootView = titleText.parent as? View
        rootView?.setBackgroundColor(android.graphics.Color.parseColor(themeColors.backgroundColor))
        // Set warna dan shadow pada title (emoji) agar kontras
        titleText.setTextColor(android.graphics.Color.parseColor("#000000"))
        titleText.setShadowLayer(4f, 0f, 2f, android.graphics.Color.parseColor("#80FFFFFF"))
        
        dialog.show()
    }
    
    private fun showThemeDialog() {
        val dialog = AlertDialog.Builder(this)
            .create()
        
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_theme_selection, null)
        dialog.setView(dialogView)
        
        // Setup theme buttons
        val boyThemeButton = dialogView.findViewById<TextView>(R.id.boyThemeButton)
        val girlThemeButton = dialogView.findViewById<TextView>(R.id.girlThemeButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
        
        // Apply current theme colors to dialog
        val themeColors = ThemeHelper.getCurrentThemeColors(this)
        dialogView.setBackgroundColor(android.graphics.Color.parseColor(themeColors.backgroundColor))
        // Set emoji color dan shadow agar kontras dengan background dialog yang terang
        boyThemeButton.setTextColor(android.graphics.Color.parseColor("#000000"))
        boyThemeButton.setShadowLayer(4f, 0f, 2f, android.graphics.Color.parseColor("#80FFFFFF"))
        girlThemeButton.setTextColor(android.graphics.Color.parseColor("#000000"))
        girlThemeButton.setShadowLayer(4f, 0f, 2f, android.graphics.Color.parseColor("#80FFFFFF"))
        
        boyThemeButton.setOnClickListener {
            ThemeHelper.applyTheme(this, ThemeHelper.THEME_BOY)
            applyCurrentTheme()
            loadDynamicMenu() // Reload menu with new theme
            dialog.dismiss()
        }
        
        girlThemeButton.setOnClickListener {
            ThemeHelper.applyTheme(this, ThemeHelper.THEME_GIRL)
            applyCurrentTheme()
            loadDynamicMenu() // Reload menu with new theme
            dialog.dismiss()
        }
        
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    private fun applySystemLanguage() {
        val currentLanguage = LanguageHelper.getCurrentLanguage(this)
        LanguageHelper.applyLanguage(this, currentLanguage)
        Log.d(TAG, "Using saved language: $currentLanguage")
    }
    
    private fun changeLanguage(language: String) {
        Log.d(TAG, "changeLanguage dipanggil dengan: $language")
        LanguageHelper.applyLanguage(this, language)

        val prefs = getSharedPreferences("language_pref", MODE_PRIVATE)
        val savedLanguage = prefs.getString("selected_language", null)
        Log.d(TAG, "[LANG] Setelah applyLanguage: selected_language=$savedLanguage, input=$language, resource config locale=${resources.configuration.locale}")
        Log.d(TAG, "[LANG] Resource folder: values-${savedLanguage ?: "default"}")
        
        Log.d(TAG, "Language changed to: $language, akan recreate MainActivity")
        recreate() // Restart MainActivity agar context dan resource update ke bahasa baru
    }
    
    private fun updateDrawerTitle() {
        try {
            runOnUiThread {
                val drawerTitle = findViewById<TextView>(R.id.drawerTitle)
                if (drawerTitle != null) {
                    try {
                        // Get current language and create new context
                        val currentLanguage = LanguageHelper.getCurrentLanguage(this)
                        val locale = Locale(currentLanguage)
                        val config = Configuration(resources.configuration)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            config.setLocale(locale)
                        } else {
                            @Suppress("DEPRECATION")
                            config.locale = locale
                        }
                        val newContext = createConfigurationContext(config)
                        
                        val newTitle = newContext.getString(R.string.main_title)
                        drawerTitle.text = newTitle
                        Log.d(TAG, "[DRAWER] Drawer title updated: $newTitle, lang=$currentLanguage, locale=${locale.language}, resource folder=values-$currentLanguage")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error updating drawer title: ${e.message}")
                    }
                } else {
                    Log.w(TAG, "Drawer title view not found")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in updateDrawerTitle: ${e.message}")
        }
    }
    
    private fun updateSubtitle() {
        try {
            runOnUiThread {
                val subtitleText = findViewById<TextView>(R.id.subtitleText)
                if (subtitleText != null) {
                    try {
                        // Get current language and create new context
                        val currentLanguage = LanguageHelper.getCurrentLanguage(this)
                        val locale = Locale(currentLanguage)
                        val config = Configuration(resources.configuration)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            config.setLocale(locale)
                        } else {
                            @Suppress("DEPRECATION")
                            config.locale = locale
                        }
                        val newContext = createConfigurationContext(config)
                        
                        val newSubtitle = newContext.getString(R.string.main_subtitle)
                        subtitleText.text = newSubtitle
                        Log.d(TAG, "Subtitle updated successfully: $newSubtitle")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error updating subtitle: ${e.message}")
                    }
                } else {
                    Log.w(TAG, "Subtitle view not found")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in updateSubtitle: ${e.message}")
        }
    }
    
    private fun changeTheme(theme: String) {
        ThemeHelper.applyTheme(this, theme)
        Log.d(TAG, "Theme changed to: $theme")
        applyCurrentTheme()
    }
    
    private fun getThemeDisplayName(theme: String): String {
        return when (theme) {
            ThemeHelper.THEME_BOY -> "Anak Laki-laki"
            ThemeHelper.THEME_GIRL -> "Anak Perempuan"
            else -> "Default"
        }
    }
    
    private fun applyCurrentTheme() {
        val themeColors = ThemeHelper.getCurrentThemeColors(this)
        // Apply background color to main content area
        findViewById<LinearLayout>(R.id.mainRoot).setBackgroundColor(android.graphics.Color.parseColor(themeColors.backgroundColor))
        // Apply title color
        findViewById<TextView>(R.id.titleText).setTextColor(android.graphics.Color.parseColor(themeColors.primaryColor))
        // Apply subtitle color
        findViewById<TextView>(R.id.subtitleText).setTextColor(android.graphics.Color.parseColor(themeColors.secondaryColor))
        // Apply header background color
        findViewById<LinearLayout>(R.id.headerLayout).setBackgroundColor(android.graphics.Color.parseColor(themeColors.headerBackgroundColor))
        // Apply theme button emoji style
        val themeButton = findViewById<TextView>(R.id.themeButton)
        val currentTheme = ThemeHelper.getCurrentTheme(this)
        themeButton.text = if (currentTheme == ThemeHelper.THEME_GIRL) "\uD83D\uDC67" else "\uD83D\uDC66"
        themeButton.setTextColor(android.graphics.Color.parseColor("#000000"))
        themeButton.setShadowLayer(4f, 0f, 2f, android.graphics.Color.parseColor("#80FFFFFF"))
        // Apply language button style
        val languageButton = findViewById<TextView>(R.id.languageButton)
        val langText = languageButton.text.toString()
        if (langText == "30f" || langText == "310") {
            languageButton.setTextColor(android.graphics.Color.parseColor("#000000"))
            languageButton.setShadowLayer(4f, 0f, 2f, android.graphics.Color.parseColor("#80FFFFFF"))
        } else {
            // Emoji bendera: shadow lebih tebal agar makin kontras
            languageButton.setShadowLayer(8f, 0f, 2f, android.graphics.Color.parseColor("#80FFFFFF"))
        }
        // Set status bar color to match theme
        window.statusBarColor = android.graphics.Color.parseColor(themeColors.headerBackgroundColor)
    }

    private fun testImageLoading() {
        Log.d(TAG, "=== Testing Image Loading ===")
        try {
            val assetManager = assets
            val testFolders = listOf("Car", "Car Front View", "Firetruck", "Lamborghini", "Monster Truck", "Police Car", "Race car", "School Bus", "Tractor", "Truck")

            testFolders.forEach { folderName ->
                val files = assetManager.list(folderName)
                Log.d(TAG, "Testing folder: $folderName")
                Log.d(TAG, "Files: ${files?.joinToString()}")

                if (files != null && files.isNotEmpty()) {
                    val sortedFiles = files.sortedBy { fileName ->
                        val numberStr = fileName.replace(Regex("[^0-9]"), "")
                        if (numberStr.isNotEmpty()) numberStr.toInt() else 0
                    }

                    val lastFile = sortedFiles.last()
                    Log.d(TAG, "Last file: $lastFile")

                    try {
                        val inputStream = assetManager.open("$folderName/$lastFile")
                        val drawable = android.graphics.drawable.Drawable.createFromStream(inputStream, null)

                        if (drawable != null) {
                            Log.d(TAG, "705 Success loading: $folderName/$lastFile")
                        } else {
                            Log.e(TAG, "74c Failed loading: $folderName/$lastFile")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "74c Error loading: $folderName/$lastFile", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in testImageLoading", e)
        }
        Log.d(TAG, "=== End Testing ===")
    }

    private fun setupRecyclerView() {
        // Set up GridLayoutManager with 2 columns
        val layoutManager = GridLayoutManager(this, 2)
        carRecyclerView.layoutManager = layoutManager

        // Create adapter with empty list initially
        carAdapter = CarMenuAdapter(this, emptyList()) { carType ->
            val intent = Intent(this, TutorialActivity::class.java)
            intent.putExtra(TutorialActivity.EXTRA_FOLDER_NAME, carType)
            tutorialLauncher.launch(intent)
        }

        carRecyclerView.adapter = carAdapter

        Log.d(TAG, "RecyclerView setup completed")
    }

    private fun loadDynamicMenu() {
        try {
            Log.d(TAG, "loadDynamicMenu called")
            val assetFolders = getAssetFolders()
            Log.d(TAG, "Found ${assetFolders.size} folders: $assetFolders")

            // Update adapter with new data
            carAdapter = CarMenuAdapter(this, assetFolders) { carType ->
                val intent = Intent(this, TutorialActivity::class.java)
                intent.putExtra(TutorialActivity.EXTRA_FOLDER_NAME, carType)
                tutorialLauncher.launch(intent)
            }
            carRecyclerView.adapter = carAdapter

            Log.d(TAG, "loadDynamicMenu completed successfully with ${assetFolders.size} items")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading menu", e)
            Toast.makeText(this, "Error loading menu: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }



    private fun getAssetFolders(): List<String> {
        val folders = mutableListOf<String>()
        val excludedFolders = setOf("images", "webkit", "stored-locales")
        try {
            val assetManager = assets
            val files = assetManager.list("")
            Log.d(TAG, "Root assets files: \\${files?.joinToString()}\\")
            files?.forEach { file ->
                // Check if it's a directory by trying to list its contents
                val subFiles = assetManager.list(file)
                if (subFiles != null && subFiles.isNotEmpty() && !excludedFolders.contains(file.lowercase())) {
                    // Keep original case for folder name
                    folders.add(file)
                    Log.d(TAG, "Found folder: \\${file} with \\${subFiles.size} files")
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error listing assets", e)
        }
        // Sort folders by name length (shortest first)
        return folders.sortedBy { it.length }
    }




    private fun getBackgroundColor(folderName: String): Int {
        return colorMap[folderName] ?: Color.parseColor("#FFF3E5F5")
    }

    private fun getTextColor(folderName: String): Int {
        return textColorMap[folderName] ?: Color.parseColor("#FF9C27B0")
    }
    

    


    private fun getFlagEmoji(languageCode: String): String {
        return when (languageCode) {
            "en" -> "\uD83C\uDDFA\uD83C\uDDF8" // 🇺🇸
            "id" -> "\uD83C\uDDEE\uD83C\uDDE9" // 🇮🇩
            "zh" -> "\uD83C\uDDE8\uD83C\uDDF3" // 🇨🇳
            "hi" -> "\uD83C\uDDEE\uD83C\uDDF3" // 🇮🇳
            "es" -> "\uD83C\uDDEA\uD83C\uDDF8" // 🇪🇸
            "ar" -> "\uD83C\uDDF8\uD83C\uDDE6" // 🇸🇦
            "fr" -> "\uD83C\uDDEB\uD83C\uDDF7" // 🇫🇷
            "pt" -> "\uD83C\uDDF5\uD83C\uDDF9" // 🇵🇹
            "bn" -> "\uD83C\uDDE7\uD83C\uDDE9" // 🇧🇩
            "ru" -> "\uD83C\uDDF7\uD83C\uDDFA" // 🇷🇺
            "ja" -> "\uD83C\uDDEF\uD83C\uDDF5" // 🇯🇵
            "de" -> "\uD83C\uDDE9\uD83C\uDDEA" // 🇩🇪
            "ur" -> "\uD83C\uDDF5\uD83C\uDDf0" // 🇵🇰
            "fa" -> "\uD83C\uDDEE\uD83C\uDDF7" // 🇮🇷
            "th" -> "\uD83C\uDDF9\uD83C\uDDED" // 🇹🇭
            "it" -> "\uD83C\uDDEE\uD83C\uDDF9" // 🇮🇹
            "ko" -> "\uD83C\uDDF0\uD83C\uDDF7" // 🇰🇷
            "ms" -> "\uD83C\uDDF2\uD83C\uDDFE" // 🇲🇾
            "vi" -> "\uD83C\uDDFB\uD83C\uDDF3" // 🇻🇳
            else -> {
                // Jika bahasa tidak didukung, fallback ke USA
                "\uD83C\uDDFA\uD83C\uDDF8" // 🇺🇸
            }
        }
    }

    private fun updateLanguageButtonFlag() {
        val prefs = getSharedPreferences("language_pref", MODE_PRIVATE)
        val savedLanguage = prefs.getString("selected_language", null)
        val languageCode = if (savedLanguage == null) {
            // Belum pernah memilih bahasa, pakai bahasa sistem
            LanguageHelper.getSystemLanguageCode()
        } else {
            savedLanguage
        }
        val flagEmoji = getFlagEmoji(languageCode)
        findViewById<TextView>(R.id.languageButton).text = flagEmoji
    }

    private fun loadCarBackground() {
        try {
            val imageView = findViewById<ImageView>(R.id.carBackgroundImage)
            // Load the last image (10.webp) from Car tutorial
            val inputStream = assets.open("Car/10.webp")
            val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(bitmap)
            inputStream.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading car background: ${e.message}")
        }
    }

    private fun updateMainTitle() {
        try {
            runOnUiThread {
                val titleText = findViewById<TextView>(R.id.titleText)
                if (titleText != null) {
                    val currentLanguage = LanguageHelper.getCurrentLanguage(this)
                    val locale = Locale(currentLanguage)
                    val config = Configuration(resources.configuration)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        config.setLocale(locale)
                    } else {
                        @Suppress("DEPRECATION")
                        config.locale = locale
                    }
                    val newContext = createConfigurationContext(config)
                    val newTitle = newContext.getString(R.string.main_title)
                    titleText.text = newTitle
                    Log.d(TAG, "Main title updated: $newTitle")
                } else {
                    Log.w(TAG, "titleText view not found")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in updateMainTitle: ${e.message}")
        }
    }

    private fun applyDrawerEmojiStyle() {
        val emojiIds = listOf(
            R.id.nav_home, R.id.nav_about, R.id.nav_help, R.id.nav_language, R.id.nav_theme
        )
        emojiIds.forEach { id ->
            val tv = findViewById<TextView>(id)
            tv?.let {
                it.setTextColor(android.graphics.Color.parseColor("#000000"))
                it.setShadowLayer(4f, 0f, 2f, android.graphics.Color.parseColor("#80FFFFFF"))
            }
        }
        // Tambahkan styling untuk header drawer
        val headerTitle = findViewById<TextView>(R.id.drawerHeaderTitle)
        headerTitle?.let {
            it.setTextColor(android.graphics.Color.parseColor("#000000"))
            it.setShadowLayer(4f, 0f, 2f, android.graphics.Color.parseColor("#80FFFFFF"))
        }
    }

    override fun onResume() {
        super.onResume()
        applyCurrentTheme()
        Log.d(TAG, "onResume: themeButton text=" + findViewById<TextView>(R.id.themeButton).text)
    }
}
