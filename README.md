<div align="center">

<img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white"/>
<img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white"/>
<img src="https://img.shields.io/badge/Min%20SDK-API%2024-blue?style=flat-square"/>
<img src="https://img.shields.io/badge/License-MIT-green?style=flat-square"/>
<img src="https://img.shields.io/badge/MindMatrix%20VTU-Project%20%2386-orange?style=flat-square"/>

# 💧 Jal-Sanchay Tracker

### *"Ek Boond Bachao, Ek Jeevan Bachao"*
**Save a Drop, Save a Life**

An Android app that turns **rainwater harvesting into a measurable, trackable goal.**  
Enter your roof area and daily rainfall — watch your *Water Wealth* grow.

[Screenshots](#-screenshots) · [Features](#-features) · [Formula](#-core-formula) · [Tech Stack](#️-tech-stack) · [Getting Started](#-getting-started)

</div>

---

## 📸 Screenshots

| ## Login Screen

![Login Screen](https://raw.githubusercontent.com/mujeebabdul1310/jal-sanchay-tracker/9c1d9600ced373a3dc46e2cf62760a9a49876411/login%20screen.jpeg)( | ![Dashboard](screenshots/dashboard.png) | ![Log](screenshots/log.png) | ![History](screenshots/history.png) |

## Sign Up Screen
![Sign Up Screen](https://raw.githubusercontent.com/mujeebabdul1310/jal-sanchay-tracker/6ec9b17b462340c424cbe50c6a60b0fecbd361ce/sign%20up%20screen.jpeg)

## Dashboard Screen
![Dashboard Screen](https://raw.githubusercontent.com/mujeebabdul1310/jal-sanchay-tracker/6ec9b17b462340c424cbe50c6a60b0fecbd361ce/dashboard%20screen.jpeg)

## History Screen
![History Screen](https://raw.githubusercontent.com/mujeebabdul1310/jal-sanchay-tracker/6ec9b17b462340c424cbe50c6a60b0fecbd361ce/history.jpeg)

## Input Field Screen
![Input Field Screen](https://raw.githubusercontent.com/mujeebabdul1310/jal-sanchay-tracker/6ec9b17b462340c424cbe50c6a60b0fecbd361ce/input%20field.jpeg)

## Rainfall Log Screen
![Rainfall Log Screen](https://raw.githubusercontent.com/mujeebabdul1310/jal-sanchay-tracker/6ec9b17b462340c424cbe50c6a60b0fecbd361ce/rainfall%20log%20screen.jpeg)

## Profile Screen
![Profile Screen](https://raw.githubusercontent.com/mujeebabdul1310/jal-sanchay-tracker/6ec9b17b462340c424cbe50c6a60b0fecbd361ce/profile%20screen.jpeg)

## Water Harvest Tip Screen
![Water Harvest Tip Screen](https://raw.githubusercontent.com/mujeebabdul1310/jal-sanchay-tracker/6ec9b17b462340c424cbe50c6a60b0fecbd361ce/wter%20harvest%20tip.jpeg)

| ![Report](screenshots/report.png) | ![Tips](screenshots/tips.png) | ![Profile](screenshots/profile.png) | ![Setup](screenshots/setup.png) |

> **Note:** To add screenshots, place images in a `screenshots/` folder in the project root.

---

## ✨ Features

| Feature | Description |
|---|---|
| 🔐 **Auth** | Firebase email/password login & registration with password-strength indicator |
| 🏠 **Setup** | Configure roof area, tank capacity, household daily usage & runoff coefficient |
| 💧 **Dashboard** | Animated water tank visual, today's savings, total savings & impact score |
| ➕ **Log Rainfall** | Live harvest preview as you enter rainfall — see litres update in real time |
| 📋 **History** | Browse all rainfall entries with swipe-to-delete functionality |
| 📈 **Report** | Monthly bar chart (MPAndroidChart) + CSV & PDF export |
| 💡 **Tips** | 12 searchable, collapsible tips for better rainwater harvesting |
| 🌊 **Impact Score** | Converts total litres saved into "household water days" |
| 👤 **Profile** | View account info, water setup summary, and recent entries in one place |

---

## 🧮 Core Formula

```
Litres Harvested = Roof Area (sq ft) × Rainfall (mm) × 0.0929 × Runoff Coefficient
```

> `0.0929` converts sq ft → sq m (1 sq ft = 0.0929 m²)

### Runoff Coefficients by Roof Type

| Roof Type | Coefficient | Efficiency |
|-----------|:-----------:|:----------:|
| Metal / Tile | 0.9 | ████████░░ 90% |
| Asphalt | 0.8 | ████████░░ 80% |
| Gravel | 0.6 | ██████░░░░ 60% |

**Example:** 200 sq ft roof · 4 mm rain · asphalt (0.8) → **59 L harvested**  
`200 × 4 × 0.0929 × 0.8 = 59.456 L`

---

## 🛠️ Tech Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| Language | Kotlin | 1.9+ |
| UI | XML Layouts + Material Design 3 | 1.11.0 |
| Architecture | MVVM (ViewModel + LiveData) | — |
| Database | Room (SQLite) | 2.6.1 |
| Auth & Backend | Firebase Authentication | — |
| Charts | MPAndroidChart | v3.1.0 |
| Async | Kotlin Coroutines | 1.7.3 |
| Local Storage | SharedPreferences | — |

---

## 🚀 Getting Started

### Prerequisites

| Tool | Version |
|------|---------|
| Android Studio | Hedgehog (2023.1.1) or newer |
| JDK | 17 |
| Android SDK | 34 |
| Min SDK | API 24 (Android 7.0+) |

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/jal-sanchay-tracker.git
   cd jal-sanchay-tracker
   ```

2. **Add your `google-services.json`**  
   Download from your [Firebase Console](https://console.firebase.google.com/) and place it at:
   ```
   app/google-services.json
   ```

3. **Open in Android Studio**
   ```
   File → Open → Select the JalSanchayTracker folder
   ```

4. **Sync Gradle**  
   Click **"Sync Now"** when prompted. Requires internet to download dependencies.

5. **Run the App**  
   Connect a device or start an emulator (API 24+) and click ▶ **Run**.

---

## 📂 Project Structure

```
app/src/main/
├── java/com/jalsanchay/tracker/
│   ├── data/
│   │   ├── RainfallEntry.kt           ← Room Entity
│   │   ├── RainfallDao.kt             ← Database queries (DAO)
│   │   ├── AppDatabase.kt             ← Room DB singleton
│   │   └── RainfallRepository.kt      ← Data source abstraction
│   ├── ui/
│   │   ├── MainActivity.kt            ← Host with Bottom Navigation
│   │   ├── MainViewModel.kt           ← Shared ViewModel
│   │   ├── SetupActivity.kt           ← First-run setup screen
│   │   ├── SplashActivity.kt          ← Launcher/splash screen
│   │   ├── LoginActivity.kt           ← Firebase email login
│   │   ├── RegisterActivity.kt        ← Account creation
│   │   ├── ProfileActivity.kt         ← User profile & water setup
│   │   ├── WaterTankView.kt           ← Custom animated tank (Canvas)
│   │   ├── adapters/
│   │   │   ├── HistoryAdapter.kt
│   │   │   └── TipsAdapter.kt
│   │   └── fragments/
│   │       ├── DashboardFragment.kt
│   │       ├── HistoryFragment.kt
│   │       ├── ReportFragment.kt
│   │       └── TipsFragment.kt
│   └── utils/
│       ├── UserPreferences.kt         ← SharedPreferences wrapper
│       └── WaterCalculator.kt         ← All math/formula logic
└── res/
    ├── layout/                        ← XML UI layouts
    ├── drawable/                      ← SVG vector icons + logo
    ├── values/                        ← Colors, strings, themes
    ├── menu/                          ← Navigation menus
    └── color/                         ← Color state lists
```

---

## 📦 Dependencies

```gradle
// Firebase
implementation platform('com.google.firebase:firebase-bom:32.7.0')
implementation 'com.google.firebase:firebase-auth-ktx'

// Room Database
implementation "androidx.room:room-runtime:2.6.1"
implementation "androidx.room:room-ktx:2.6.1"
kapt "androidx.room:room-compiler:2.6.1"

// Lifecycle (ViewModel + LiveData)
implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0"
implementation "androidx.lifecycle:lifecycle-livedata-ktx:2.7.0"

// MPAndroidChart
implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'

// Coroutines
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"

// Material Design
implementation "com.google.android.material:material:1.11.0"
```

---

## ✅ Completed Features

- [x] Firebase Authentication (email/password login & registration)
- [x] Water tank visual fills relative to data entered (animated)
- [x] Live harvest preview while entering rainfall
- [x] Monthly report with bar chart (MPAndroidChart)
- [x] CSV & PDF data export
- [x] Input validation with graceful error messages
- [x] Searchable tips section (12 tips)
- [x] Impact Score: litres → household water days
- [x] Historical data stored in Room DB (offline-first)
- [x] Runoff coefficient configurable by roof type
- [x] Profile screen with water setup summary

---

## 🎨 Design Highlights

- **Dark navy ocean theme** — reinforces the water conservation message
- **Animated WaterTankView** — custom Canvas view with wave animation & gradient fill
- **Offline-first** — all data stored locally; no internet needed after initial setup
- **Lifecycle-aware** — LiveData + ViewModel prevents memory leaks
- **Material Design 3** — consistent cards, chips, and bottom navigation

---

## 🌍 Impact

Water scarcity affects over **4 billion people** globally. Jal-Sanchay Tracker empowers individual households to:

- 📏 Measure how much rainwater they can realistically harvest
- 📆 Track consistency and build conservation habits over time
- 🌱 Understand their personal contribution to water sustainability

> Aligned with **UN SDG Goal 6** — Clean Water & Sanitation for All

---

## 👨‍💻 Author

Built as part of the **MindMatrix VTU Internship Program — Project #86**

---

## 📄 License

```
MIT License — free to use, modify, and distribute with attribution.
```

---

<div align="center">

💧 *Every drop counts. Start tracking yours today.* 💧

</div>

