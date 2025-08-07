# AdMob Debug Guide

## Quick Fixes

### 1. Test Device Setup
Add your device as test device in onCreate:

```kotlin
// Add before MobileAds.initialize
val testDeviceIds = listOf("33BE2250B43518CCDA7DE426D04EE231")
val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
MobileAds.setRequestConfiguration(configuration)
```

### 2. Check Logs
Run this command to see ad logs:
```bash
adb logcat | grep TutorialActivity
```

### 3. Current Ad Unit IDs
- Banner: `ca-app-pub-3940256099942544/6300978111`
- Interstitial: `ca-app-pub-3940256099942544/1033173712`
- Rewarded: `ca-app-pub-3940256099942544/5224354917`

### 4. Common Issues
1. **No Internet**: Check network connection
2. **Wrong App ID**: Verify in AndroidManifest.xml
3. **Ad Not Loaded**: Check logs for error messages
4. **Test Device**: Add device to test device list

### 5. Debug Status
The app now logs detailed ad status. Check logs for:
- Ad loading success/failure
- Error codes and messages
- Ad display attempts 