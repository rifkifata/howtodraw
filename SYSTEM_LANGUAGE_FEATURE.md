# 🌍 Fitur Mengikuti Bahasa Sistem Android

## 📱 Deskripsi Fitur

Aplikasi menggambar bunga sekarang dapat secara otomatis mengikuti bahasa sistem Android. Fitur ini memungkinkan aplikasi untuk:

1. **Otomatis menggunakan bahasa sistem** saat pertama kali dibuka
2. **Mengikuti perubahan bahasa sistem** secara real-time
3. **Memberikan opsi manual** untuk memilih bahasa tertentu
4. **Menyimpan preferensi pengguna** untuk bahasa yang dipilih

## 🔧 Cara Kerja

### 1. Deteksi Bahasa Sistem
- Aplikasi mendeteksi bahasa sistem Android menggunakan `Locale.getDefault()`
- Mendukung 19 bahasa: Indonesia, Inggris, Cina, Hindi, Spanyol, Arab, Prancis, Portugis, Bengali, Rusia, Jepang, Jerman, Urdu, Persia, Thai, Italia, Korea, Melayu, Vietnam

### 2. Mode Mengikuti Sistem
- **Default**: Aplikasi mengikuti bahasa sistem secara otomatis
- **Switch Control**: Pengguna dapat mengaktifkan/menonaktifkan fitur ini
- **Real-time Update**: Aplikasi memperbarui bahasa saat sistem berubah

### 3. Mode Manual
- Pengguna dapat memilih bahasa tertentu
- Ketika bahasa manual dipilih, mode "ikuti sistem" dinonaktifkan
- Pengaturan tersimpan di SharedPreferences

## 🎯 Implementasi Teknis

### LanguageHelper.kt
```kotlin
// Fitur baru yang ditambahkan:
- shouldFollowSystemLanguage(): Boolean
- setFollowSystemLanguage(context, follow: Boolean)
- checkAndUpdateSystemLanguage(context)
- getCurrentLanguage() - dimodifikasi untuk mendukung mode sistem
```

### MainActivity.kt & TutorialActivity.kt
```kotlin
// Method baru:
- applySystemLanguage() - menerapkan bahasa sistem saat startup
- onConfigurationChanged() - menangani perubahan konfigurasi sistem
```

### Dialog Bahasa
- **Switch "Follow System Language"** di bagian atas dialog
- **Daftar bahasa manual** di bawah switch
- **Auto-apply** saat switch diaktifkan

## 📋 Cara Penggunaan

### Untuk Pengguna:
1. **Buka aplikasi** - bahasa sistem otomatis diterapkan
2. **Ubah bahasa sistem Android** - aplikasi akan mengikuti (jika mode aktif)
3. **Pilih bahasa manual** - buka menu bahasa dan pilih bahasa tertentu
4. **Aktifkan/nonaktifkan mode sistem** - gunakan switch di dialog bahasa

### Untuk Developer:
1. **LanguageHelper.shouldFollowSystemLanguage()** - cek apakah mode sistem aktif
2. **LanguageHelper.checkAndUpdateSystemLanguage()** - update bahasa jika sistem berubah
3. **LanguageHelper.setFollowSystemLanguage()** - set mode sistem

## 🔄 Flow Aplikasi

```
Aplikasi Dibuka
       ↓
Cek First Launch?
       ↓
   Ya → Terapkan Bahasa Sistem
       ↓
   Tidak → Cek Mode Sistem?
       ↓
   Aktif → Terapkan Bahasa Sistem
       ↓
   Tidak → Terapkan Bahasa Tersimpan
       ↓
Aplikasi Siap
```

## 🛠️ Konfigurasi

### AndroidManifest.xml
```xml
android:configChanges="locale|layoutDirection"
```
- Menangani perubahan locale dan layout direction
- Mencegah restart activity saat bahasa berubah

### SharedPreferences
```kotlin
KEY_FOLLOW_SYSTEM = "follow_system_language" // Boolean
KEY_LANGUAGE = "selected_language" // String
```

## 🌐 Bahasa yang Didukung

| Bahasa | Kode | Bendera |
|--------|------|---------|
| Indonesia | id | 🇮🇩 |
| English | en | 🇺🇸 |
| 中文 | zh | 🇨🇳 |
| हिन्दी | hi | 🇮🇳 |
| Español | es | 🇪🇸 |
| العربية | ar | 🇸🇦 |
| Français | fr | 🇫🇷 |
| Português | pt | 🇵🇹 |
| বাংলা | bn | 🇧🇩 |
| Русский | ru | 🇷🇺 |
| 日本語 | ja | 🇯🇵 |
| Deutsch | de | 🇩🇪 |
| اردو | ur | 🇵🇰 |
| فارسی | fa | 🇮🇷 |
| ไทย | th | 🇹🇭 |
| Italiano | it | 🇮🇹 |
| 한국어 | ko | 🇰🇷 |
| Bahasa Melayu | ms | 🇲🇾 |
| Tiếng Việt | vi | 🇻🇳 |

## 🐛 Troubleshooting

### Masalah Umum:
1. **Bahasa tidak berubah**: Cek apakah mode sistem aktif
2. **Aplikasi crash**: Pastikan bahasa sistem didukung
3. **UI tidak update**: Restart aplikasi setelah perubahan bahasa

### Log Debug:
```kotlin
Log.d(TAG, "Current language: $currentLanguage")
Log.d(TAG, "System language: $systemLanguage")
Log.d(TAG, "Follow system language: ${LanguageHelper.shouldFollowSystemLanguage(this)}")
```

## 📈 Keuntungan

1. **User Experience**: Aplikasi otomatis menggunakan bahasa yang familiar
2. **Accessibility**: Mendukung pengguna dengan preferensi bahasa berbeda
3. **Maintenance**: Lebih mudah mengelola bahasa dengan sistem terpusat
4. **Flexibility**: Memberikan opsi manual dan otomatis

## 🔮 Pengembangan Selanjutnya

- [ ] Deteksi bahasa berdasarkan lokasi
- [ ] Auto-translate untuk bahasa yang belum didukung
- [ ] Backup/restore pengaturan bahasa
- [ ] Analytics untuk penggunaan bahasa 