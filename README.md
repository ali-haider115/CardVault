# Card Vault — Digital Card Wallet

**Card Vault** is an Android application designed for organizing, managing, and securing personal digital cards (such as Student IDs, Library passes, Membership cards, Loyalty cards, and Passes) locally on your mobile device.

Built strictly using **Java** and **traditional Android XML layouts**, the app operates completely offline without requiring any database, Firebase, or external backend services.

---

## 🌟 Key Features

### 🔐 1. Authentication & Security
- **Local Account Management:** Register and log in locally with account credentials stored in `users.json`.
- **Salted SHA-256 Hashing:** All user passwords and security recovery answers are hashed using salted SHA-256 encryption (`SecurityUtils`) before saving.
- **Forgot & Reset Password:** Demonstration recovery flow using customizable Security Questions and normalized answer verification.
- **Session Management:** Login sessions persist in `SharedPreferences`. Logging out clears active sessions and clears the Activity back stack to prevent unauthenticated access.

### 💳 2. Digital Card Wallet & Storage
- **System Photo Picker Integration:** Upload separate front and back pictures of cards safely from device gallery without requiring runtime storage permissions.
- **Internal Storage Persistence:** Card images are copied into the app's internal private storage directory (`card_images/`) with unique `UUID` filenames.
- **Local Metadata Persistence:** Card details (Card ID, Name, Category, Image Paths, Creation Timestamp) are persisted locally in `cards.json`.

### 📊 3. Dashboard & Quick Actions Grid
- **Personalized Header:** Dynamic welcome banner displaying user's full name and total saved card count.
- **Horizontal Card Slider:** Swipeable horizontal `RecyclerView` displaying front images of all saved cards.
- **2x3 Quick Action Grid:** `GridLayout` feature tiles with vector icons and labels navigating to My Cards, Add Card, Categories, Profile, Security, and Settings.
- **Empty State Guidance:** Displays helpful hints and quick-add actions when no cards exist.

### 🖼️ 4. Interactive Card Details & Full-Screen Viewer
- **Side-by-Side Viewing:** `CardDetailsActivity` displays front and back images clearly with original aspect ratio preservation (`scaleType="fitCenter"`).
- **Enlarged Full-Screen Image Viewer:** Tap either card image to open `FullScreenImageActivity` displaying the picture full-screen against a dark background.
- **Edit & Delete Actions:**
  - **Edit Card:** Modify card name, category, or replace front/back images independently. Automatically cleans up old unused image files.
  - **Delete Card:** Prompts `AlertDialog` confirmation and permanently deletes card metadata and associated image files.

### 📂 5. Filtering & Categorization
- **My Cards Screen:** View the full list of saved cards in a vertical list layout with instant search/refresh upon returning.
- **Categories Screen:** Interactive `ChipGroup` filtering cards by category (*Student ID*, *Library*, *Membership*, *Loyalty*, *Transport*, *Business*, *Other*). Displays custom empty state notices for categories with no saved cards.

### 👤 6. Profile Customization
- **Circular Profile Avatar:** Upload and retain custom profile pictures in internal storage (`profile_pictures/`).
- **Profile Details:** Edit and save Full Name, change account passwords via verification dialog, and log out safely.

---

## 🏛️ Mandatory Layouts & Architectural Compliance

This application demonstrates all four foundational Android XML layout types:

| Layout Type | Primary XML Implementation Screens |
| :--- | :--- |
| **`ConstraintLayout`** | Login, Registration, Forgot Password, Reset Password, Card Details |
| **`RelativeLayout`** | Splash Screen, Profile Header, Full-Screen Image Viewer |
| **`LinearLayout`** | Add Card Form, Edit Card Form, My Cards, Categories |
| **`GridLayout`** | Dashboard Quick Action Feature Grid |

### 📱 Alternative Resources & Responsiveness
- **Landscape Orientation (`res/layout-land/`):** Custom landscape XML layouts for key screens (`activity_splash.xml`, `activity_login.xml`, `activity_register.xml`, `activity_forgot_password.xml`, `activity_reset_password.xml`, `activity_dashboard.xml`, `activity_add_card.xml`, `activity_card_details.xml`, `activity_profile.xml`). Every landscape layout uses 100% identical View IDs to eliminate runtime null pointer issues.
- **Dark Mode Support (`res/values-night/`):** Complete dark theme configuration (`themes.xml` and `colors.xml`) adapting background surfaces, card containers, stroke borders, and text colors.

---

## 📁 Project Structure

```
CardVault/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/cardvault/
│   │   │   │   ├── SplashActivity.java
│   │   │   │   ├── LoginActivity.java
│   │   │   │   ├── RegisterActivity.java
│   │   │   │   ├── ForgotPasswordActivity.java
│   │   │   │   ├── ResetPasswordActivity.java
│   │   │   │   ├── DashboardActivity.java
│   │   │   │   ├── AddCardActivity.java
│   │   │   │   ├── MyCardsActivity.java
│   │   │   │   ├── CategoriesActivity.java
│   │   │   │   ├── CardDetailsActivity.java
│   │   │   │   ├── FullScreenImageActivity.java
│   │   │   │   ├── EditCardActivity.java
│   │   │   │   ├── ProfileActivity.java
│   │   │   │   ├── CardSliderAdapter.java
│   │   │   │   ├── models/
│   │   │   │   │   ├── UserModel.java
│   │   │   │   │   └── CardModel.java
│   │   │   │   └── utils/
│   │   │   │       ├── SecurityUtils.java
│   │   │   │       ├── SessionManager.java
│   │   │   │       ├── UserStorageManager.java
│   │   │   │       └── CardStorageManager.java
│   │   │   ├── res/
│   │   │   │   ├── drawable/          # Brand vectors, overlays, button backgrounds
│   │   │   │   ├── layout/            # Portrait XML layouts
│   │   │   │   ├── layout-land/       # Landscape XML layouts
│   │   │   │   ├── mipmap-anydpi-v26/ # Adaptive launcher icons
│   │   │   │   ├── values/            # Colors, strings, themes
│   │   │   │   └── values-night/      # Dark mode colors & themes
│   │   │   └── AndroidManifest.xml
├── build.gradle.kts
└── README.md
```

---

## 🛠️ Prerequisites & Building

- **Android Studio:** 2026.1+
- **Build System:** Gradle (AGP 9.4.1)
- **Programming Language:** Java (JDK 11 compatibility)
- **Min SDK:** API 24 (Android 7.0)
- **Target / Compile SDK:** API 37

### Building via Terminal
To compile and assemble the debug APK:
```bash
./gradlew app:assembleDebug
```

---

## 🚀 App Screen Inventory

1. **Splash Screen** (`SplashActivity`)
2. **Login Screen** (`LoginActivity`)
3. **Registration Screen** (`RegisterActivity`)
4. **Forgot Password Screen** (`ForgotPasswordActivity`)
5. **Reset Password Screen** (`ResetPasswordActivity`)
6. **Dashboard Screen** (`DashboardActivity`)
7. **Add Card Screen** (`AddCardActivity`)
8. **My Cards Screen** (`MyCardsActivity`)
9. **Categories Screen** (`CategoriesActivity`)
10. **Card Details Screen** (`CardDetailsActivity`)
11. **Full-Screen Image Viewer** (`FullScreenImageActivity`)
12. **Edit Card Screen** (`EditCardActivity`)
13. **Profile Screen** (`ProfileActivity`)

---

## 📄 License & Coursework Notice

Developed for educational and coursework demonstration purposes as a local digital card wallet application on Android. All data and image storage operations are handled strictly on the local device.