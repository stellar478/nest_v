# Nest — Secure Password & Secret Vault

Nest is a production-grade, offline-first Android password and secret manager crafted with modern **Jetpack Compose**, **Material 3**, and hardware-backed security. Designed with a clean Scandinavian aesthetic, Nest provides zero-knowledge AES-256 encryption, Android BiometricPrompt hardware authentication (Fingerprint & Facial Recognition), local versioning, and offline sync conflict resolution.

---

## 🔒 Key Security & Architecture Features

- **Zero-Knowledge Encryption**: AES-256 encrypted local vault payloads; master credentials and encryption keys never leave the device unencrypted.
- **Biometric Hardware Unlock**: Seamless integration with Android's `BiometricPrompt` API for fingerprint and face recognition secondary verification.
- **Offline-First Sync & Conflict Resolver**: Detects concurrent offline edits across devices and provides a visual side-by-side conflict merge interface.
- **Security Audit Engine**: Evaluates master password strength, identifies reused/pwned credentials, and recommends rotation timelines.
- **Scandinavian Design System**: Custom Material 3 theme featuring dynamic light and dark modes, generous negative space, and skeleton loading states.

---

## 📋 Prerequisites

Before running the app, ensure you have the following installed on your machine:

- **Git** (v2.20+)
- **Android Studio** (Ladybug, Jellyfish, or newer recommended)
- **JDK 17** (Bundled with modern Android Studio releases)
- **Android SDK Platform** (API 34 / Android 14 recommended, Minimum API 26 / Android 8.0)
- An **Android Virtual Device (AVD)** with API 26+ or a physical Android phone with **USB Debugging** enabled.

---

## 🚀 How to Run the Application Using GitHub

### Step 1: Clone the Repository

Open your terminal or command prompt and clone the repository using Git:

```bash
git clone <repository-url>
cd <repository-directory>
```

---

### Step 2: Open in Android Studio

1. Launch **Android Studio**.
2. Click on **Open** (or **File > Open**).
3. Select the cloned repository directory.
4. Wait for Android Studio to index files and run the **Gradle Sync**. Gradle will automatically download all required Kotlin, Jetpack Compose, and AndroidX dependencies (including `androidx.biometric`).

---

### Step 3: Run on Emulator or Physical Device

#### Option A: Using Android Studio GUI (Recommended)
1. In Android Studio, open the **Device Manager** and start an Android Virtual Device (AVD) or connect a physical Android device via USB.
2. Select `app` from the run configuration dropdown at the top bar.
3. Click the **Run** button (green play icon `▶`) or press `Shift + F10` (Windows/Linux) / `Control + R` (macOS).

#### Option B: Using Gradle Command Line (CLI)
Make sure an emulator is running or a device is connected via ADB (`adb devices`), then run:

```bash
# Build the Debug APK
./gradlew assembleDebug

# Install and launch directly on connected device/emulator
./gradlew installDebug
```

---

### Step 4: Testing Biometric Recognition on Android Emulator

To test Fingerprint or Facial Recognition on an Android Emulator:
1. Open the **Settings** app inside the Android Emulator.
2. Navigate to **Security > Fingerprint** (or **Face Unlock**).
3. Set a PIN/Passcode and enroll at least one simulated fingerprint (use the Extended Controls menu `...` in the emulator sidebar under **Fingerprint** to send touch signals).
4. Launch Nest and toggle **Hardware Biometric Unlock** in **Settings** or click **Biometric Passcode** on the Lock Screen.

---

## 🧪 Running Automated Tests

To execute unit tests and Robolectric JVM test suites:

```bash
./gradlew testDebugUnitTest
```

---

## 🎨 Tech Stack & Libraries

- **Language**: 100% Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Biometric Security**: `androidx.biometric:biometric`
- **State & Architecture**: Jetpack ViewModel, `StateFlow`, `collectAsStateWithLifecycle()`
- **Navigation**: Type-safe Navigation Compose with `@Serializable`
- **Data Storage**: Modern DataStore & Local Encryption Providers
- **Design System**: Nest Custom Component System (Skeleton shimmer loaders, dialogs, badges, bottom bars)
