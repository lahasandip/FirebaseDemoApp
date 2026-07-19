# Marketplace Demo App (Firebase & Jetpack Compose)

A professional-grade real-time Marketplace application built with modern Android practices and the full Firebase suite.

## 🚀 Implemented Firebase Services

The app utilizes **7+ Firebase services** to provide a complete backend infrastructure without a custom server:

1.  **Firebase Authentication**: 
    *   Secure Email/Password sign-up and login.
    *   Automatic session management (remembers logged-in users).
2.  **Cloud Firestore (NoSQL Database)**:
    *   **Real-time Products**: Syncs marketplace listings across all users instantly.
    *   **Real-time Chat**: Powering instant messaging between buyers and sellers.
    *   **User Profiles**: Stores display names, bios, and notification tokens.
3.  **Firebase Cloud Messaging (FCM)**:
    *   **Push Notifications**: Alerts sellers when they receive a new message.
    *   **Deep-Linking**: Clicking a notification opens the specific chat room directly.
4.  **Firebase Remote Config**:
    *   **Promo Banners**: Control a "Dynamic Offer" banner at the top of the Home Screen from the cloud.
    *   **Immediate Updates**: Configured with a 0-second fetch interval for real-time UI changes.
5.  **Firebase Crashlytics**: 
    *   Real-time monitoring of app stability and bug reporting.
6.  **Firebase Performance Monitoring**: 
    *   Tracks app startup time and screen rendering speeds.
7.  **Google Analytics**: 
    *   Tracks user engagement and interaction patterns.

---

## 🛠️ How the App Works

### 1. The Core Experience
*   **Login/Sign up Screen**
*   **Home Screen**
*   **Listing Creation**
*   **Product Details**
*   **

### 2. Real-time Communication (The Chat System)
*   **Unified Chat Rooms**
*   **Modern UI**
*   **Auto-Scroll**
*   **Message List**

### 3. Automatic Notifications
*   **Direct Pings**
*   **Background Listener**
*   **High Priority**

### 4. Security & Best Practices
*   **Secrets Management**
*   **MVVM Architecture**
*   **Clean UI**

---
## 🚀 Getting Started
1.  Add your `google-services.json` to the `app/` folder.
2.  Enable **Email Auth**, **Firestore**, and **Remote Config** in the Firebase Console.
3.  Run the app and start trading!
