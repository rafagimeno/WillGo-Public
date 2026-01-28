plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // plugin para la serialization
    //id("org.jetbrains.kotlin.plugin.serialization") version "1.5.10"
    //kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
    alias(libs.plugins.compose.compiler)

}

android {
    namespace = "com.example.willgo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.willgo"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    //implementation(libs.androidx.media3.common.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //DB
    implementation("io.github.jan-tennert.supabase:postgrest-kt:3.0.0")
    implementation("io.ktor:ktor-client-android:3.0.0-rc-1")

    //SERIALIZATION
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

    //APIS
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    //ICONS
    implementation ("androidx.compose.material:material-icons-extended:$1.6.7")

    //NAVIGATION
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation("androidx.navigation:navigation-compose:2.7.1")

    //COIL IMAGES
    implementation ("io.coil-kt:coil-compose:2.1.0")
    //GOOGLE MAPS
    implementation("com.google.android.gms:play-services-maps:18.0.2")
    implementation("com.google.maps.android:maps-compose:1.0.0")

    //ESPRESSO TESTING

}