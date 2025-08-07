# AdMob Troubleshooting Guide

## Masalah Umum dan Solusi

### 1. Iklan Tidak Muncul

#### Kemungkinan Penyebab:
- **Test Device**: Pastikan device Anda terdaftar sebagai test device
- **Network**: Periksa koneksi internet
- **Ad Unit ID**: Pastikan ID iklan benar
- **App ID**: Pastikan App ID di AndroidManifest.xml benar

#### Solusi:
1. **Tambahkan Test Device**:
   ```kotlin
   // Tambahkan di onCreate sebelum MobileAds.initialize
   val testDeviceIds = listOf("33BE2250B43518CCDA7DE426D04EE231")
   val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
   MobileAds.setRequestConfiguration(configuration)
   ```

2. **Periksa Logs**:
   - Buka Logcat di Android Studio
   - Filter dengan tag "TutorialActivity"
   - Cari pesan error iklan

### 2. Banner Ad Tidak Muncul

#### Periksa:
- Layout file `activity_tutorial.xml`
- AdView ID: `bannerAdView`
- Ad Unit ID: `ca-app-pub-3940256099942544/6300978111`

### 3. Interstitial Ad Tidak Muncul

#### Periksa:
- Ad Unit ID: `ca-app-pub-3940256099942544/1033173712`
- Trigger: Middle step (step 5 dari 10, step 10 dari 20)
- Log: "Showing interstitial ad at step X"

### 4. Rewarded Ad Tidak Muncul

#### Periksa:
- Ad Unit ID: `ca-app-pub-3940256099942544/5224354917`
- Trigger: Last step access
- Log: "Showing rewarded ad"

### 5. Test Ad Unit IDs

Jika iklan production tidak berfungsi, gunakan test IDs:

```kotlin
// Test Ad Unit IDs
private const val BANNER_TEST_ID = "ca-app-pub-3940256099942544/6300978111"
private const val INTERSTITIAL_TEST_ID = "ca-app-pub-3940256099942544/1033173712"
private const val REWARDED_TEST_ID = "ca-app-pub-3940256099942544/5224354917"
```

### 6. Debugging Steps

1. **Periksa Logs**:
   ```
   adb logcat | grep TutorialActivity
   ```

2. **Periksa Network**:
   - Pastikan device terhubung internet
   - Coba di jaringan berbeda

3. **Test dengan Test Device**:
   - Tambahkan device ID Anda ke test device list
   - Test dengan test ad unit IDs

4. **Periksa AdMob Console**:
   - Login ke AdMob Console
   - Periksa status app dan ad units
   - Pastikan ad units aktif

### 7. Common Error Codes

- **ERROR_CODE_INTERNAL_ERROR (0)**: Internal error
- **ERROR_CODE_INVALID_REQUEST (1)**: Invalid ad request
- **ERROR_CODE_NETWORK_ERROR (2)**: Network error
- **ERROR_CODE_NO_FILL (3)**: No ad available
- **ERROR_CODE_APP_ID_MISSING (8)**: App ID missing

### 8. Production Checklist

Sebelum release:
- [ ] Ganti test ad unit IDs dengan production IDs
- [ ] Hapus test device configuration
- [ ] Test di device production
- [ ] Periksa AdMob policy compliance
- [ ] Test di jaringan berbeda

## Log Messages untuk Debug

Cari log messages ini di Logcat:

```
✅ AdMob initialization completed
✅ Banner ad loaded successfully
✅ Interstitial ad loaded successfully
✅ Rewarded ad loaded successfully
❌ Banner ad failed to load: [error message]
❌ Interstitial ad failed to load: [error message]
❌ Rewarded ad failed to load: [error message]
``` 