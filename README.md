# Okariru — Mobile (Android)

App Android untuk aplikasi pinjaman/koperasi **Okariru** (nasabah/customer). Konsumsi REST API dari [okariru-be](https://github.com/jasnse/okariru-be).

- Repo: https://github.com/jasnse/Okariru-Mobile
- Package: `com.project.binar.okariru`
- Base URL API: `http://35.184.39.133:8080/` (di-set lewat `BuildConfig.BASE_URL`, lihat `app/build.gradle.kts`)

## Tech Stack

- **Kotlin** + **Jetpack Compose** (Material 3)
- **Hilt** — dependency injection
- **Retrofit2** + **OkHttp** + **kotlinx.serialization** — networking (JSON converter: kotlinx serialization, bukan Gson meski dependency-nya ada)
- **Room** — local database
- **DataStore Preferences** — local key-value storage (session, dsb.)
- **Navigation Compose** (dengan `safeargs` + `@Serializable` routes)
- **Coil** — image loading
- **Firebase** (Analytics, Cloud Messaging/FCM untuk push notification)
- **Chucker** — network inspector (hanya di build debug)

Konfigurasi: `minSdk 29`, `targetSdk 37`, `compileSdk 37`.

## Struktur Package

```
com.project.binar.okariru
├── core/            # network module (Retrofit/OkHttp/Json setup), notification, service (FCM)
├── data/            # per fitur: <fitur>/dto, <fitur>/remote (Retrofit API), <fitur>/repository
│   ├── auth/        # login, register, customer profile
│   ├── pinjaman/    # produk & pengajuan pinjaman
│   ├── angsuran/    # cicilan
│   ├── plafond/     # limit pinjaman
│   ├── document/    # upload dokumen
│   ├── reset/       # forgot/reset password (OTP)
│   └── status_pinjaman/
├── di/              # Hilt modules
├── presentation/    # UI per fitur (Compose): login, register, home, landing_page,
│                     # Pinjaman, angsuran, profile, reset, security, splash_screen,
│                     # status_pinjaman, navigation, shared/component
└── ui/              # theme
```

## Menjalankan Project

1. Clone repo, buka dengan **Android Studio** (versi yang support AGP terbaru, Kotlin 2.x).
2. Sync Gradle (`gradlew` sudah include wrapper).
3. **Signing key** (untuk build release): buat `keystore.properties` di root project (jangan commit!):
   ```properties
   storeFile=path/to/keystore.jks
   storePassword=...
   keyAlias=...
   keyPassword=...
   ```
   Bisa juga lewat environment variable: `ANDROID_KEYSTORE_PATH`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD`.
4. Jalankan di emulator/device (`minSdk 29`+).

### Build Signed APK (release)

```bash
./gradlew assembleRelease
```

- Release build pakai **R8/ProGUard** (`isMinifyEnabled = true`, `isShrinkResources = true`) — rules ada di `app/proguard-rules.pro`. Kalau nambah model/DTO baru untuk Retrofit (kotlinx.serialization), pastikan class-nya tetap kena rule `@Serializable` keep di package `com.project.binar.okariru.**`, atau tambahkan rule spesifik kalau taruh di package lain.
- Debug build pakai Chucker untuk inspect network request/response (`adb shell` notification atau shake device, tergantung setup).

## Autentikasi & Session

- Login → dapat JWT dari BE, disimpan di **DataStore** (lihat `data/auth`, `AuthSession`/`AuthUser`).
- Token dikirim via header `Authorization: Bearer <token>` di request yang butuh auth (lihat interceptor di `core/network`).
- FCM token di-register/update ke BE setelah login (`updateFcmToken` di `AuthApi`) dan dibersihkan saat logout (`clearFcmToken`).

## Fitur Utama

- Register & login customer
- Lihat plafond (limit pinjaman)
- Ajukan pinjaman (pilih produk, isi form, upload dokumen)
- Lihat status pengajuan pinjaman
- Lihat & bayar angsuran
- Reset password via OTP email
- Push notification (FCM)
- Edit profile

## Terkait

- Backend (Spring Boot): https://github.com/jasnse/okariru-be
- Dokumen desain/slicing (Figma → Compose) & RBAC: ada di project BE, folder `C:\Binar\project` (`0*-*.md`, `RBAC.md`)
