plugins {
    id("com.android.application")
    id("kotlin-android")
    id ("kotlin-kapt")
    id("com.google.devtools.ksp")
}

android {
    namespace = "ru.dl.checklist"
    compileSdk = 33

    defaultConfig {
        applicationId = "ru.dl.checklist"
        minSdk = 29
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19
    }
    kotlinOptions {
        jvmTarget = "19"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation( libs.timber)
    implementation (libs.dagger)
    kapt (libs.dagger.compiler)
    implementation (libs.androidx.room.guava)
    implementation (libs.androidx.room.ktx)
    implementation (libs.androidx.room.paging)
    implementation (libs.androidx.room.runtime)
//    kapt ("androidx.room:room-compiler:2.5.1")
    ksp(libs.androidx.room.compiler)
}