# Tutorial Menggambar Bunga untuk Anak-Anak

Aplikasi Android yang menyenangkan dan edukatif yang dirancang untuk mengajarkan anak-anak cara menggambar berbagai jenis bunga melalui tutorial interaktif step-by-step dengan dukungan multi-bahasa dan tema yang dapat disesuaikan.

## 🌸 Fitur Utama

### **Tutorial Step-by-Step**
- **10+ Jenis Bunga**: Bird of Paradise, Bleeding Heart, Cartoon Flowers, Cosmos, Daisy, Dogwood, Iris, Jasmine, Marigold, dan lainnya
- **Langkah Bertahap**: Setiap tutorial terdiri dari 9-20 langkah yang mudah diikuti
- **Navigasi Intuitif**: Swipe kiri/kanan untuk melihat langkah selanjutnya
- **Indikator Progress**: Dots indicator menunjukkan posisi langkah saat ini

### **Multi-Language Support**
- **20+ Bahasa**: Indonesia, Inggris, Arab, Cina, Jepang, Korea, Thailand, Vietnam, dan lainnya
- **Language Selection**: Dialog pemilihan bahasa yang mudah digunakan
- **Localized Content**: Semua teks dan instruksi tersedia dalam bahasa lokal

### **Theme System**
- **Light/Dark Mode**: Tema terang dan gelap yang dapat disesuaikan
- **Boy/Girl Themes**: Tema khusus untuk anak laki-laki dan perempuan
- **Theme Selection**: Dialog pemilihan tema yang interaktif

### **User Interface**
- **Material Design**: Menggunakan komponen Material Design modern
- **Responsive Layout**: Beradaptasi dengan berbagai ukuran layar
- **Smooth Animations**: Transisi dan animasi yang halus
- **Accessibility**: Dukungan untuk pengguna dengan kebutuhan khusus

## 🎨 Jenis Bunga yang Tersedia

### **Realistic Flowers**
- **Bird of Paradise Flower** - Bunga cendrawasih yang eksotis
- **Bleeding Heart Flower** - Bunga hati yang berdarah
- **Cosmos** - Bunga cosmos yang elegan
- **Daisy Flower** - Bunga aster yang klasik
- **Dogwood Flowers** - Bunga dogwood yang indah
- **Iris** - Bunga iris yang unik
- **Jasmine** - Bunga melati yang harum
- **Marigold** - Bunga marigold yang cerah

### **Cartoon Flowers**
- **Cartoon Flowers** - Bunga kartun yang lucu
- **Cartoon Sunflower** - Bunga matahari kartun

## 📱 Screenshots & UI Components

### **Main Menu**
- Grid layout dengan kartu bunga yang menarik
- Setiap kartu menampilkan preview hasil akhir
- Animasi ripple effect saat disentuh
- Header dengan judul dan subtitle yang informatif

### **Tutorial Activity**
- ViewPager untuk navigasi antar langkah
- Progress dots indicator
- Tombol kembali dan navigasi
- Gambar tutorial yang jelas dan detail

### **Settings Dialogs**
- Language selection dialog dengan flag icons
- Theme selection dialog dengan preview
- Smooth transitions dan animations

## 🛠️ Detail Teknis

### **Technology Stack**
- **Language**: Kotlin 100%
- **Minimum SDK**: API 26 (Android 8.0)
- **Target SDK**: API 35 (Android 15)
- **UI Framework**: Android Views dengan ConstraintLayout
- **Design System**: Material Design 3
- **Image Format**: PNG, WebP support

### **Architecture**
- **Activity-based**: MainActivity dan TutorialActivity
- **Adapter Pattern**: RecyclerView adapters untuk list dan grid
- **Utility Classes**: ImageUtils, LanguageHelper, ThemeHelper
- **Resource Management**: Proper resource organization

### **Key Components**
- **BaseActivity**: Base class untuk common functionality
- **LanguageAdapter**: Adapter untuk language selection
- **ProgressDotsAdapter**: Adapter untuk progress indicator
- **MenuItem**: Data class untuk menu items

## 📁 Struktur Project

```
tescursor/
├── app/
│   ├── build.gradle.kts          # App-level build configuration
│   └── src/main/
│       ├── AndroidManifest.xml   # App manifest
│       ├── assets/               # Tutorial images
│       │   ├── Bird of Paradise Flower/
│       │   │   ├── 1.png        # Step 1
│       │   │   ├── 2.png        # Step 2
│       │   │   └── 10.png       # Final result
│       │   ├── Bleeding Heart Flower/
│       │   ├── Cartoon Flowers/
│       │   ├── Cosmos/
│       │   ├── Daisy Flower/
│       │   ├── Dogwood Flowers/
│       │   ├── Iris/
│       │   ├── Jasmine/
│       │   └── Marigold/
│       ├── java/com/htd/flower/
│       │   ├── BaseActivity.kt           # Base activity class
│       │   ├── ImageUtils.kt             # Image processing utilities
│       │   ├── LanguageAdapter.kt        # Language selection adapter
│       │   ├── LanguageHelper.kt         # Language management
│       │   ├── MainActivity.kt           # Main menu activity
│       │   ├── MenuItem.kt               # Menu item data class
│       │   ├── ProgressDotsAdapter.kt    # Progress dots adapter
│       │   ├── ThemeHelper.kt            # Theme management
│       │   └── TutorialActivity.kt       # Tutorial display activity
│       └── res/
│           ├── drawable/                 # Drawable resources
│           │   ├── button_background.xml
│           │   ├── card_ripple.xml
│           │   ├── flag_*.xml            # Country flags
│           │   └── ic_*.xml              # Icons
│           ├── layout/                   # Layout files
│           │   ├── activity_main.xml     # Main menu layout
│           │   ├── activity_tutorial.xml # Tutorial layout
│           │   ├── dialog_language_selection.xml
│           │   ├── dialog_theme_selection.xml
│           │   ├── header_layout.xml
│           │   ├── item_language.xml
│           │   ├── item_progress_dot.xml
│           │   └── item_tutorial_step.xml
│           ├── menu/
│           │   └── nav_menu.xml          # Navigation menu
│           └── values/                   # Resource values
│               ├── arrays.xml            # Array resources
│               ├── colors.xml            # Color definitions
│               ├── dimens.xml            # Dimension values
│               ├── strings.xml           # String resources
│               ├── themes.xml            # Theme definitions
│               └── values-*/             # Localized resources
│                   └── strings.xml       # Localized strings
├── build.gradle.kts              # Project-level build configuration
├── gradle/                       # Gradle wrapper
├── gradle.properties             # Gradle properties
└── settings.gradle.kts           # Project settings
```

## 🌍 Supported Languages

### **Complete Localization**
- **Arabic** (ar) - العربية
- **Bengali** (bn) - বাংলা
- **Chinese** (zh) - 中文
- **English** (en) - English
- **French** (fr) - Français
- **German** (de) - Deutsch
- **Hindi** (hi) - हिन्दी
- **Indonesian** (id) - Bahasa Indonesia
- **Italian** (it) - Italiano
- **Japanese** (ja) - 日本語
- **Korean** (ko) - 한국어
- **Malay** (ms) - Bahasa Melayu
- **Persian** (fa) - فارسی
- **Portuguese** (pt) - Português
- **Russian** (ru) - Русский
- **Spanish** (es) - Español
- **Thai** (th) - ไทย
- **Urdu** (ur) - اردو
- **Vietnamese** (vi) - Tiếng Việt

## 🎯 Tutorial Steps

### **Generic Tutorial Structure**
1. **Step 1**: Mulai dengan menggambar bentuk dasar bunga
2. **Step 2**: Tambahkan detail pada bagian tengah
3. **Step 3**: Gambar kelopak pertama
4. **Step 4**: Lengkapi dengan kelopak lainnya
5. **Step 5**: Tambahkan detail pada kelopak
6. **Step 6**: Gambar batang dan daun
7. **Step 7**: Lengkapi dengan detail daun
8. **Step 8**: Tambahkan bayangan dan tekstur
9. **Step 9**: Perbaiki detail dan garis
10. **Step 10**: Lengkapi dengan sentuhan akhir
11. **Step 11**: Hasil akhir yang sempurna

### **Specialized Tutorials**
- **Cosmos**: 10 langkah dengan fokus pada kelopak tipis dan batang panjang
- **Iris**: 11 langkah dengan detail kelopak yang unik
- **Marigold**: 10 langkah dengan kelopak berlapis
- **Jasmine**: 10 langkah dengan kelopak halus

## 🚀 Building & Running

### **Prerequisites**
- Android Studio Arctic Fox atau lebih baru
- Android SDK API 26+
- Gradle 8.0+

### **Setup Instructions**
1. Clone repository ini
2. Buka project di Android Studio
3. Sync Gradle files
4. Connect device atau start emulator
5. Run aplikasi dengan `Shift + F10`

### **Build Variants**
- **Debug**: Untuk development dan testing
- **Release**: Untuk production dengan optimizations

## 📦 Adding New Content

### **Adding New Flower Tutorial**
1. Buat folder baru di `app/src/main/assets/`
2. Tambahkan gambar tutorial dengan penomoran (1.png, 2.png, dst.)
3. Tambahkan string resources untuk deskripsi langkah
4. Aplikasi akan otomatis mendeteksi folder baru

### **Adding New Language**
1. Buat folder `values-[language_code]/` di `res/`
2. Copy `strings.xml` dari `values/`
3. Translate semua string ke bahasa target
4. Test dengan mengubah language di aplikasi

## 🎨 Customization

### **Themes**
- **Light Theme**: Clean dan bright untuk siang hari
- **Dark Theme**: Nyaman untuk mata di malam hari
- **Boy Theme**: Warna-warna yang disukai anak laki-laki
- **Girl Theme**: Warna-warna yang disukai anak perempuan

### **Colors**
- Primary colors dapat disesuaikan di `colors.xml`
- Theme colors diatur di `themes.xml`
- Support untuk night mode di `values-night/themes.xml`

## 🔧 Configuration

### **Gradle Configuration**
```kotlin
android {
    compileSdk = 35
    defaultConfig {
        minSdk = 26
        targetSdk = 35
    }
}
```

### **Dependencies**
- **AndroidX Core**: Core Android functionality
- **Material Design**: UI components
- **ConstraintLayout**: Advanced layouts
- **ViewPager2**: Modern ViewPager implementation

## 📊 Performance

### **Optimizations**
- **Image Loading**: Efficient bitmap loading
- **Memory Management**: Proper bitmap recycling
- **Layout Optimization**: Efficient view hierarchies
- **Resource Management**: Optimized resource usage

### **Memory Usage**
- **Low Memory Footprint**: Efficient image handling
- **Bitmap Recycling**: Prevents memory leaks
- **Lazy Loading**: Images loaded on demand

## 🐛 Troubleshooting

### **Common Issues**
1. **Gradle Sync Failed**: Update Gradle version
2. **Build Errors**: Clean and rebuild project
3. **Runtime Crashes**: Check logcat for error details
4. **Image Loading Issues**: Verify asset file paths

### **Debug Mode**
- Enable debug logging in `ImageUtils.kt`
- Check logcat for detailed error messages
- Use Android Studio's debugger for step-by-step debugging

## 🤝 Contributing

### **Guidelines**
1. Fork repository
2. Create feature branch
3. Make changes
4. Test thoroughly
5. Submit pull request

### **Code Style**
- Follow Kotlin coding conventions
- Use meaningful variable names
- Add comments for complex logic
- Maintain consistent formatting

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- **Material Design**: For the beautiful UI components
- **Android Community**: For the excellent documentation
- **Flower Artists**: For the beautiful tutorial images
- **Translators**: For the multi-language support

## 📞 Support

Jika Anda menemukan bug atau memiliki saran untuk fitur baru, silakan buat issue di repository ini.

---

**Made with ❤️ for children's education and creativity** #   h o w t o d r a w  
 