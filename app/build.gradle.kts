plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services") version "4.4.2"
    id("com.google.devtools.ksp") version "2.0.0-1.0.21" apply false
}

android {
    namespace = "kh.sothun.darachhat.rupp.fe.ecommerce_app"
    compileSdk =  36

    defaultConfig {
        applicationId = "kh.sothun.darachhat.rupp.fe.ecommerce_app"
        minSdk = 25
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.4.0")) // Firebase BoM
    implementation("com.google.firebase:firebase-database-ktx")        // Realtime DB KTX

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.viewpager2)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.github.bumptech.glide:glide:5.0.5")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("com.tbuonomo:dotsindicator:5.1.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")
}