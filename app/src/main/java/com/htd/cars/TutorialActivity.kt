package com.htd.cars

import android.app.Dialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.InputStream
import java.util.*
import android.app.AlertDialog
import android.widget.Switch
import android.widget.Toast
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.FullScreenContentCallback


class TutorialActivity : BaseActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tutorialAdapter: TutorialAdapter
    private lateinit var drawerLayout: androidx.drawerlayout.widget.DrawerLayout
    private lateinit var navigationView: LinearLayout
    private var tutorialSteps: List<TutorialStep> = emptyList()
    private lateinit var progressDotsAdapter: ProgressDotsAdapter
    
    // Interstitial ad variables
    private var interstitialAd: InterstitialAd? = null
    private var adShown = false
    
    // Reward ad variables
    private var rewardedAd: RewardedAd? = null
    private var rewardAdShown = false
    private var isWaitingForReward = false
    private var countDownTimer: android.os.CountDownTimer? = null
    private var countdownDialog: AlertDialog? = null
    
    private val interstitialAdUnitId: String by lazy { getString(R.string.interstitial_ad_unit_id) }
    private val rewardedAdUnitId: String by lazy { getString(R.string.rewarded_ad_unit_id) }
    
    companion object {
        private const val TAG = "TutorialActivity"
        const val EXTRA_FOLDER_NAME = "folder_name"
        const val EXTRA_FOLDER_TITLE = "folder_title"
        private const val WAIT_TIME_MILLIS = 30000L // 30 seconds
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Force portrait orientation
        requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        
        // Apply system language automatically
        applySystemLanguage()
        
        setContentView(R.layout.activity_tutorial)
        
        // Initialize views
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        
        // Apply current theme
        applyCurrentTheme()
        
        // Initialize AdMob and load banner ad
        MobileAds.initialize(this) { initializationStatus ->
            Log.d(TAG, "AdMob initialization completed")
            val statusMap = initializationStatus.adapterStatusMap
            for ((adapter, status) in statusMap) {
                Log.d(TAG, "Adapter: $adapter, Status: ${status.initializationState}, Latency: ${status.latency}")
            }
            
            // Load ads after initialization
            loadBannerAd()
            loadInterstitialAd()
            loadRewardedAd()
        }
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        val folderName = intent.getStringExtra(EXTRA_FOLDER_NAME) ?: ""
        val folderTitle = intent.getStringExtra(EXTRA_FOLDER_TITLE) ?: folderName.replaceFirstChar { it.uppercase() }
        
        Log.d(TAG, "🎯 TutorialActivity started with folder: $folderName, title: $folderTitle")
        

        
        // Test image loading first
        testImageLoading(folderName)
        
        setupViews()
        setupNavigationDrawer()
        loadTutorialSteps(folderName)
        
        // Load flower background image
        loadFlowerBackground()
        
        // Update language button flag
        updateLanguageButtonFlag()
        
        // Update drawer title
        updateDrawerTitle()
        
        progressDotsAdapter = ProgressDotsAdapter(this, tutorialSteps.size)
        val progressDotsRecyclerView = findViewById<RecyclerView>(R.id.progressDotsRecyclerView)
        progressDotsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        progressDotsRecyclerView.adapter = progressDotsAdapter
        
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                progressDotsAdapter.updateCurrentPosition(position)
                Log.d(TAG, "📱 Page changed to position: $position")
                showInterstitialAdIfNeeded(position)
                checkRewardAdForLastStep(position)
            }
        })
        
        // Check ad status after 3 seconds for debugging
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            checkAdStatus()
        }, 3000)
        
        applyDrawerEmojiStyle()
    }
    
    override fun onResume() {
        super.onResume()
        applyCurrentTheme()
    }
    
    private fun applyCurrentTheme() {
        val themeColors = ThemeHelper.getCurrentThemeColors(this)
        
        // Apply background color to main content area
        findViewById<View>(R.id.main).setBackgroundColor(android.graphics.Color.parseColor(themeColors.backgroundColor))
        
        // Apply header background color
        findViewById<LinearLayout>(R.id.headerLayout).setBackgroundColor(android.graphics.Color.parseColor(themeColors.headerBackgroundColor))
        
        // Apply title color
        findViewById<TextView>(R.id.tutorialTitle).setTextColor(android.graphics.Color.parseColor(themeColors.primaryColor))
        
        // Apply home button style
        val homeButton = findViewById<TextView>(R.id.homeButton)
        homeButton.setTextColor(android.graphics.Color.parseColor("#000000"))
        homeButton.setShadowLayer(4f, 0f, 2f, android.graphics.Color.parseColor("#80FFFFFF"))
        
        // Apply theme button emoji style
        val themeButton = findViewById<TextView>(R.id.themeButton)
        val currentTheme = ThemeHelper.getCurrentTheme(this)
        themeButton.text = if (currentTheme == ThemeHelper.THEME_GIRL) "👧" else "👦"
        themeButton.setTextColor(android.graphics.Color.parseColor("#000000"))
        themeButton.setShadowLayer(4f, 0f, 2f, android.graphics.Color.parseColor("#80FFFFFF"))
        
        // Apply language button style
        val languageButton = findViewById<TextView>(R.id.languageButton)
        val langText = languageButton.text.toString()
        if (langText == "🌏" || langText == "🌐") {
            languageButton.setTextColor(android.graphics.Color.parseColor("#000000"))
            languageButton.setShadowLayer(4f, 0f, 2f, android.graphics.Color.parseColor("#80FFFFFF"))
        } else {
            // Emoji bendera: shadow lebih tebal agar makin kontras
            languageButton.setShadowLayer(8f, 0f, 2f, android.graphics.Color.parseColor("#80FFFFFF"))
        }
        
        // Update status bar color
        window.statusBarColor = android.graphics.Color.parseColor(themeColors.headerBackgroundColor)
        
        // Note: Language button flag should not be colored by theme to preserve flag colors
        
        // Tambahkan styling drawer emoji/icon
        applyDrawerEmojiStyle()
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
    }
    
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Tidak perlu lagi cek atau update bahasa sistem
    }
    
    private fun setupViews() {
        // Setup ViewPager2
        viewPager = findViewById(R.id.tutorialViewPager)
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        
        // Optimize ViewPager2 for smooth swiping
        viewPager.offscreenPageLimit = 1 // Preload adjacent pages
        viewPager.setPageTransformer { page, position ->
            // Simple fade animation for smoother transitions
            page.alpha = 1f - kotlin.math.abs(position) * 0.3f
        }
        
        // Additional optimizations for smooth swiping
        viewPager.getChildAt(0)?.let { recyclerView ->
            if (recyclerView is RecyclerView) {
                recyclerView.setHasFixedSize(true)
                recyclerView.setItemViewCacheSize(3)
                recyclerView.isDrawingCacheEnabled = true
                recyclerView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
            }
        }
        
        // Setup language settings button
        findViewById<TextView>(R.id.languageButton).setOnClickListener {
            showLanguageDialog()
        }
        
        // Setup theme settings button
        findViewById<TextView>(R.id.themeButton).setOnClickListener {
            showThemeDialog()
        }
        
        // Setup burger menu button
        findViewById<TextView>(R.id.burgerButton).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        // Setup home button
        findViewById<TextView>(R.id.homeButton).setOnClickListener {
            finish()
        }
    }
    
    private fun loadTutorialSteps(folderName: String) {
        try {
            Log.d(TAG, "🔄 Starting to load tutorial steps for folder: $folderName")
            
            val assetManager = assets
            
            // First, let's check what folders are available in assets
            val rootFiles = assetManager.list("")
            Log.d(TAG, "📁 Root assets files: ${rootFiles?.joinToString(", ") ?: "null"}")
            
            // Try to find the exact folder name or case-insensitive match
            var actualFolderName = folderName
            if (rootFiles != null) {
                val matchingFolder = rootFiles.find { it.equals(folderName, ignoreCase = true) }
                if (matchingFolder != null) {
                    actualFolderName = matchingFolder
                    Log.d(TAG, "✅ Found matching folder: $actualFolderName (original: $folderName)")
                    
                    // Update title with actual folder name
                    findViewById<TextView>(R.id.tutorialTitle).text = actualFolderName
                } else {
                    Log.w(TAG, "⚠️ No matching folder found for: $folderName")
                }
            }
            
            val files = assetManager.list(actualFolderName)
            
            Log.d(TAG, "📁 Found ${files?.size ?: 0} files in folder: $actualFolderName")
            Log.d(TAG, "📄 Files: ${files?.joinToString(", ") ?: "null"}")
            
            if (files != null && files.isNotEmpty()) {
                // Sort files by number to get correct order
                val sortedFiles = files.sortedBy { fileName ->
                    val numberStr = fileName.replace(Regex("[^0-9]"), "")
                    if (numberStr.isNotEmpty()) numberStr.toInt() else 0
                }
                
                Log.d(TAG, "📋 Sorted files: ${sortedFiles.joinToString(", ")}")
                
                // Create tutorial steps
                tutorialSteps = sortedFiles.mapIndexed { index, fileName ->
                    TutorialStep(
                        stepNumber = index + 1,
                        fileName = fileName,
                        folderName = actualFolderName,
                        categoryKey = actualFolderName.lowercase()
                    )
                }
                
                Log.d(TAG, "✅ Created ${tutorialSteps.size} tutorial steps")
                
                // Setup adapter with progress dot callback
                tutorialAdapter = TutorialAdapter(this, tutorialSteps)
                viewPager.adapter = tutorialAdapter
                
            } else {
                Log.e(TAG, "❌ No files found in folder: $actualFolderName")
                // Show error message to user
                findViewById<TextView>(R.id.tutorialTitle).text = getString(R.string.error_no_tutorial_files)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error loading tutorial steps", e)
            // Show error message to user
            findViewById<TextView>(R.id.tutorialTitle).text = getString(R.string.error_loading_tutorial, e.message ?: "")
        }
    }
    
    private fun testImageLoading(folderName: String) {
        Log.d(TAG, "🧪 Testing image loading for folder: $folderName")
        try {
            val assetManager = assets
            val files = assetManager.list(folderName)
            Log.d(TAG, "📁 Test - Found ${files?.size ?: 0} files in folder: $folderName")
            
            if (files != null && files.isNotEmpty()) {
                val testFile = files.first()
                Log.d(TAG, "🧪 Testing with file: $testFile")
                
                try {
                    val inputStream = assetManager.open("$folderName/$testFile")
                    val bytes = inputStream.readBytes()
                    inputStream.close()
                    
                    Log.d(TAG, "📊 Test - File size: ${bytes.size} bytes")
                    
                    val options = BitmapFactory.Options().apply {
                        inPreferredConfig = Bitmap.Config.ARGB_8888
                    }
                    
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                    
                    if (bitmap != null) {
                        Log.d(TAG, "✅ Test - Successfully loaded: $testFile (${bitmap.width}x${bitmap.height})")
                    } else {
                        Log.e(TAG, "❌ Test - Failed to decode: $testFile")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Test - Error loading: $testFile", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Test - Error in testImageLoading", e)
        }
    }
    
    private fun setupNavigationDrawer() {
        findViewById<TextView>(R.id.nav_home).setOnClickListener {
            Log.d(TAG, "[DRAWER] nav_home clicked, closing drawer and finishing activity")
            drawerLayout.closeDrawer(GravityCompat.START)
            finish() // Go back to main activity
        }
        findViewById<TextView>(R.id.nav_about).setOnClickListener {
            Log.d(TAG, "[DRAWER] nav_about clicked")
            drawerLayout.closeDrawer(GravityCompat.START)
            // TODO: Show about dialog
        }
        findViewById<TextView>(R.id.nav_help).setOnClickListener {
            Log.d(TAG, "[DRAWER] nav_help clicked")
            drawerLayout.closeDrawer(GravityCompat.START)
            // TODO: Show help dialog
        }
        findViewById<TextView>(R.id.nav_language).setOnClickListener {
            Log.d(TAG, "[DRAWER] nav_language clicked, showing language dialog")
            drawerLayout.closeDrawer(GravityCompat.START)
            showLanguageDialog()
        }
        findViewById<TextView>(R.id.nav_theme).setOnClickListener {
            Log.d(TAG, "[DRAWER] nav_theme clicked, showing theme dialog")
            drawerLayout.closeDrawer(GravityCompat.START)
            showThemeDialog()
        }
    }
    
    private fun showLanguageDialog() {
        val languages = LanguageHelper.getAvailableLanguages()
        val languageCodes = LanguageHelper.getSupportedLanguageCodes().toTypedArray()
        val currentLanguageCode = LanguageHelper.getCurrentLanguage(this)
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_language_selection)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        // Set dialog size to match MainActivity
        val window = dialog.window
        window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.languageRecyclerView)
        val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)
        
        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = LanguageAdapter(languages, languageCodes, currentLanguageCode) { selectedLanguageCode ->
            changeLanguage(selectedLanguageCode)
            dialog.dismiss()
        }
        recyclerView.adapter = adapter
        
        // Setup cancel button
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        
        // Set dialog background sama seperti dialog tema
        val themeColors = ThemeHelper.getCurrentThemeColors(this)
        val titleText = dialog.findViewById<TextView>(R.id.dialogTitle)
        val rootView = titleText?.parent as? View
        rootView?.setBackgroundColor(android.graphics.Color.parseColor(themeColors.backgroundColor))
        // Set warna dan shadow pada title (emoji) agar kontras
        titleText?.setTextColor(android.graphics.Color.parseColor("#000000"))
        titleText?.setShadowLayer(4f, 0f, 2f, android.graphics.Color.parseColor("#80FFFFFF"))
        
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
            changeTheme(ThemeHelper.THEME_BOY)
            dialog.dismiss()
        }
        
        girlThemeButton.setOnClickListener {
            changeTheme(ThemeHelper.THEME_GIRL)
            dialog.dismiss()
        }
        
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    private fun applySystemLanguage() {
        // Selalu gunakan bahasa yang dipilih user (manual), tidak ada mode follow system
        val currentLanguage = LanguageHelper.getCurrentLanguageName(this)
        LanguageHelper.applyLanguage(this, currentLanguage)
        Log.d(TAG, "applySystemLanguage - Using saved language: $currentLanguage")
    }
    
    private fun changeLanguage(language: String) {
        LanguageHelper.applyLanguage(this, language)
        Log.d(TAG, "Language changed to: $language")
        val currentLanguage = LanguageHelper.getCurrentLanguage(this)
        Log.d(TAG, "Current language after applyLanguage: $currentLanguage")
        recreate() // Restart activity agar context dan resource update ke bahasa baru
    }
    
    private fun changeTheme(theme: String) {
        ThemeHelper.applyTheme(this, theme)
        Log.d(TAG, "Theme changed to: $theme")
        applyCurrentTheme()
        progressDotsAdapter.notifyDataSetChanged()
        
        // Log untuk memastikan tema tersimpan
        val savedTheme = ThemeHelper.getCurrentTheme(this)
        Log.d(TAG, "Theme saved and retrieved: $savedTheme")
    }
    
    private fun updateLanguageButtonFlag() {
        try {
            val currentLanguage = LanguageHelper.getCurrentLanguage(this)
            val languageButton = findViewById<TextView>(R.id.languageButton)
            
            val flagEmoji = when (currentLanguage) {
                "en" -> "🇺🇸"
                "id" -> "🇮🇩"
                "zh" -> "🇨🇳"
                "hi" -> "🇮🇳"
                "es" -> "🇪🇸"
                "ar" -> "🇸🇦"
                "fr" -> "🇫🇷"
                "pt" -> "🇵🇹"
                "bn" -> "🇧🇩"
                "ru" -> "🇷🇺"
                "ja" -> "🇯🇵"
                "de" -> "🇩🇪"
                "ur" -> "🇵🇰"
                "fa" -> "🇮🇷"
                "th" -> "🇹🇭"
                "it" -> "🇮🇹"
                "ko" -> "🇰🇷"
                "ms" -> "🇲🇾"
                "vi" -> "🇻🇳"
                else -> "🇺🇸"
            }
            
            languageButton.text = flagEmoji
        } catch (e: Exception) {
            Log.e(TAG, "Error updating language button flag: ${e.message}")
        }
    }
    
    private fun updateDrawerTitle() {
        try {
            val drawerTitle = findViewById<TextView>(R.id.drawerTitle)
            if (drawerTitle != null) {
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
            }
        } catch (e: Exception) {
            Log.e(TAG, "[DRAWER] Error updating drawer title: ${e.message}")
        }
    }
    
    private fun loadFlowerBackground() {
        try {
            val flowerBackgroundImage = findViewById<ImageView>(R.id.carBackgroundImage)
            val assetManager = assets
            
            // Load the last image from Cosmos folder as background
            val files = assetManager.list("Cosmos")
            if (files != null && files.isNotEmpty()) {
                val sortedFiles = files.sortedBy { fileName ->
                    val numberStr = fileName.replace(Regex("[^0-9]"), "")
                    if (numberStr.isNotEmpty()) numberStr.toInt() else 0
                }
                
                val lastFile = sortedFiles.last()
                val inputStream = assetManager.open("Cosmos/$lastFile")
                val drawable = android.graphics.drawable.Drawable.createFromStream(inputStream, null)
                
                if (drawable != null) {
                    flowerBackgroundImage.setImageDrawable(drawable)
                    Log.d(TAG, "Flower background loaded: Cosmos/$lastFile")
                } else {
                    Log.e(TAG, "Failed to load flower background")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading flower background", e)
        }
    }
    
    private fun loadBannerAd() {
        try {
            val adView = findViewById<com.google.android.gms.ads.AdView>(R.id.bannerAdView)
            if (adView != null) {
                val adRequest = com.google.android.gms.ads.AdRequest.Builder().build()
                adView.adListener = object : com.google.android.gms.ads.AdListener() {
                    override fun onAdLoaded() {
                        Log.d(TAG, "Banner ad loaded successfully")
                    }
                    
                    override fun onAdFailedToLoad(loadAdError: com.google.android.gms.ads.LoadAdError) {
                        Log.e(TAG, "Banner ad failed to load: ${loadAdError.message}")
                        Log.e(TAG, "Banner ad error code: ${loadAdError.code}")
                    }
                    
                    override fun onAdOpened() {
                        Log.d(TAG, "Banner ad opened")
                    }
                    
                    override fun onAdClicked() {
                        Log.d(TAG, "Banner ad clicked")
                    }
                }
                adView.loadAd(adRequest)
                Log.d(TAG, "Banner ad loading started")
            } else {
                Log.e(TAG, "Banner ad view not found in layout")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading banner ad", e)
        }
    }
    
    private fun loadInterstitialAd() {
        try {
            Log.d(TAG, "Loading interstitial ad with unit ID: $interstitialAdUnitId")
            val adRequest = AdRequest.Builder().build()
            
            InterstitialAd.load(
                this,
                interstitialAdUnitId,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        Log.d(TAG, "Interstitial ad loaded successfully")
                        
                        // Set ad callbacks
                        ad.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                interstitialAd = null
                                Log.d(TAG, "Interstitial ad was dismissed")
                            }
                            
                            override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                                interstitialAd = null
                                Log.e(TAG, "Interstitial ad failed to show: ${adError.message}")
                                Log.e(TAG, "Interstitial ad error code: ${adError.code}")
                            }
                            
                            override fun onAdShowedFullScreenContent() {
                                Log.d(TAG, "Interstitial ad showed full screen content")
                            }
                        }
                    }
                    
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        interstitialAd = null
                        Log.e(TAG, "Interstitial ad failed to load: ${loadAdError.message}")
                        Log.e(TAG, "Interstitial ad error code: ${loadAdError.code}")
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error loading interstitial ad", e)
        }
    }
    
    private fun loadRewardedAd() {
        try {
            Log.d(TAG, "Loading rewarded ad with unit ID: $rewardedAdUnitId")
            val adRequest = AdRequest.Builder().build()
            
            RewardedAd.load(
                this,
                rewardedAdUnitId,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        rewardedAd = ad
                        Log.d(TAG, "Rewarded ad loaded successfully")
                        
                        // Set ad callbacks
                        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                rewardedAd = null
                                Log.d(TAG, "Rewarded ad was dismissed")
                            }
                            
                            override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                                rewardedAd = null
                                Log.e(TAG, "Rewarded ad failed to show: ${adError.message}")
                                Log.e(TAG, "Rewarded ad error code: ${adError.code}")
                            }
                            
                            override fun onAdShowedFullScreenContent() {
                                Log.d(TAG, "Rewarded ad showed full screen content")
                            }
                        }
                    }
                    
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        rewardedAd = null
                        Log.e(TAG, "Rewarded ad failed to load: ${loadAdError.message}")
                        Log.e(TAG, "Rewarded ad error code: ${loadAdError.code}")
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error loading rewarded ad", e)
        }
    }
    
    private fun showInterstitialAdIfNeeded(currentPosition: Int) {
        if (adShown) {
            Log.d(TAG, "Interstitial ad already shown in this tutorial")
            return
        }
        
        val totalSteps = tutorialSteps.size
        val middleStep = totalSteps / 2
        
        Log.d(TAG, "Checking interstitial ad: position=$currentPosition, middleStep=$middleStep, totalSteps=$totalSteps, adLoaded=${interstitialAd != null}")
        
        // Show ad when user swipes from middle step to next step
        if (currentPosition == middleStep && interstitialAd != null) {
            Log.d(TAG, "Showing interstitial ad at step $currentPosition")
            interstitialAd?.show(this)
            adShown = true
        } else {
            Log.d(TAG, "Interstitial ad conditions not met: position=$currentPosition, middleStep=$middleStep, adLoaded=${interstitialAd != null}")
        }
    }
    
    private fun checkRewardAdForLastStep(position: Int) {
        val totalSteps = tutorialSteps.size
        val lastStepIndex = totalSteps - 1
        
        // Check if user is trying to go to the last step and reward ad hasn't been shown yet
        if (position == lastStepIndex - 1 && !rewardAdShown && !isWaitingForReward) {
            // Prevent automatic navigation to last step
            viewPager.setCurrentItem(position, false)
            
            // Show dialog with options
            showRewardAdDialog()
        }
    }
    
    private fun showRewardAdDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.reward_dialog_title))
            .setMessage(getString(R.string.reward_dialog_message))
            .setPositiveButton(getString(R.string.reward_dialog_wait_button)) { _, _ ->
                startCountdownTimer()
            }
            .setNegativeButton(getString(R.string.reward_dialog_watch_ad_button)) { _, _ ->
                showRewardedAd()
            }
            .setCancelable(false)
            .create()
        
        dialog.show()
    }
    
    private fun startCountdownTimer() {
        isWaitingForReward = true
        
        // Create countdown dialog with custom layout
        val dialogView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            setPadding(40, 40, 40, 40)
            
            val countdownText = TextView(this@TutorialActivity).apply {
                textSize = 32f
                gravity = android.view.Gravity.CENTER
                setTextColor(android.graphics.Color.parseColor("#FF6B35"))
                text = getString(R.string.countdown_time_remaining, 30)
            }
            
            val messageText = TextView(this@TutorialActivity).apply {
                textSize = 16f
                gravity = android.view.Gravity.CENTER
                setTextColor(android.graphics.Color.parseColor("#666666"))
                text = getString(R.string.countdown_dialog_message)
                setPadding(0, 20, 0, 0)
            }
            
            addView(countdownText)
            addView(messageText)
        }
        
        countdownDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.countdown_dialog_title))
            .setView(dialogView)
            .setCancelable(false)
            .create()
        
        countdownDialog?.show()
        
        countDownTimer = object : android.os.CountDownTimer(WAIT_TIME_MILLIS, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                val countdownText = dialogView.getChildAt(0) as TextView
                countdownText.text = getString(R.string.countdown_time_remaining, secondsRemaining.toInt())
                Log.d(TAG, "Countdown: $secondsRemaining seconds remaining")
            }
            
            override fun onFinish() {
                isWaitingForReward = false
                rewardAdShown = true
                countdownDialog?.dismiss()
                countdownDialog = null
                // Allow navigation to last step
                viewPager.setCurrentItem(tutorialSteps.size - 1, true)
                Log.d(TAG, "Countdown finished, allowing navigation to last step")
            }
        }.start()
        
        Log.d(TAG, "Started 30-second countdown timer with popup")
    }
    
    private fun showRewardedAd() {
        Log.d(TAG, "Attempting to show rewarded ad: adLoaded=${rewardedAd != null}")
        
        if (rewardedAd != null) {
            Log.d(TAG, "Showing rewarded ad")
            rewardedAd?.show(this) { rewardItem: RewardItem ->
                // Handle reward
                Log.d(TAG, "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
                rewardAdShown = true
                // Allow navigation to last step
                viewPager.setCurrentItem(tutorialSteps.size - 1, true)
            }
        } else {
            Log.e(TAG, "Rewarded ad not loaded, falling back to countdown timer")
            // Fallback: start countdown timer
            startCountdownTimer()
        }
    }
    
    private fun checkAdStatus() {
        Log.d(TAG, "=== AD STATUS CHECK ===")
        Log.d(TAG, "Banner Ad: ${findViewById<com.google.android.gms.ads.AdView>(R.id.bannerAdView) != null}")
        Log.d(TAG, "Interstitial Ad: ${interstitialAd != null}")
        Log.d(TAG, "Rewarded Ad: ${rewardedAd != null}")
        Log.d(TAG, "Interstitial Ad Shown: $adShown")
        Log.d(TAG, "Rewarded Ad Shown: $rewardAdShown")
        Log.d(TAG, "Tutorial Steps: ${tutorialSteps.size}")
        Log.d(TAG, "Current Position: ${viewPager.currentItem}")
        Log.d(TAG, "======================")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        countdownDialog?.dismiss()
        countdownDialog = null
    }
    
    override fun finish() {
        val resultIntent = android.content.Intent()
        if (::viewPager.isInitialized) {
            resultIntent.putExtra("last_viewpager_position", viewPager.currentItem)
        }
        setResult(android.app.Activity.RESULT_OK, resultIntent)
        super.finish()
    }
}

data class TutorialStep(
    val stepNumber: Int,
    val fileName: String,
    val folderName: String,
    val categoryKey: String
)

class TutorialAdapter(
    private val context: Context,
    private val tutorialSteps: List<TutorialStep>
) : RecyclerView.Adapter<TutorialAdapter.TutorialViewHolder>() {
    companion object {
        private const val TAG = "TutorialAdapter"
    }
    class TutorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stepImageView: ImageView = itemView.findViewById(R.id.stepImageView)
        val stepNumberText: TextView = itemView.findViewById(R.id.stepNumberText)
        val stepDescriptionText: TextView = itemView.findViewById(R.id.stepDescriptionText)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutorialViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_tutorial_step, parent, false)
        return TutorialViewHolder(view)
    }
    override fun onBindViewHolder(holder: TutorialViewHolder, position: Int) {
        val step = tutorialSteps[position]
        holder.stepNumberText.text = step.stepNumber.toString()
        val desc = getStepDescription(step.categoryKey, step.stepNumber)
        Log.d("TutorialAdapter", "Lang: ${context.resources.configuration.locale}, Step: ${step.categoryKey} ${step.stepNumber}, Desc: $desc")
        holder.stepDescriptionText.text = desc
        loadStepImage(holder.stepImageView, step)
        applyThemeColors(holder)
    }
    override fun getItemCount(): Int = tutorialSteps.size
    
    private fun getStepDescription(categoryKey: String, stepNumber: Int): String {
        // Use dynamic string key: step_{stepNumber}_{categoryKey}, fallback to generic if not found
        val key = "step_${stepNumber}_" + categoryKey.lowercase().replace(" ", "_")
        val resId = context.resources.getIdentifier(key, "string", context.packageName)
        return if (resId != 0) context.getString(resId)
            else context.getString(R.string.step_generic, stepNumber)
    }

    private fun loadStepImage(imageView: ImageView, step: TutorialStep) {
        val assetUri = "file:///android_asset/${step.folderName}/${step.fileName}"
        Glide.with(imageView.context)
            .load(assetUri)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(imageView)
    }

    private fun applyThemeColors(holder: TutorialViewHolder) {
        val themeColors = ThemeHelper.getCurrentThemeColors(context)

        // Apply theme color to step number background
        val stepNumberBackground = holder.stepNumberText.background
        if (stepNumberBackground is android.graphics.drawable.GradientDrawable) {
            stepNumberBackground.setColor(android.graphics.Color.parseColor(themeColors.primaryColor))
        }

        // Apply theme color to description text
        holder.stepDescriptionText.setTextColor(android.graphics.Color.parseColor(themeColors.secondaryColor))
    }
} 