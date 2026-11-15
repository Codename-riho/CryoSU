plugins {
    id("com.android.application")
}

android {
    namespace = "org.cryosu.espfix"
    compileSdk = 37

    defaultConfig {
        applicationId = "org.cryosu.espfix"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // Agar APK rilis langsung bisa dipasang (LSPosed tidak memeriksa tanda tangan).
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // Kelas API Xposed disediakan oleh LSPosed saat runtime; jangan dikemas ke APK.
    compileOnly(project(":xposed-stubs"))
}
