
plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.nav.safeargs)
    alias(libs.plugins.secrets)
    alias(libs.plugins.ksp)
}

android {
    namespace = "ru.dl.checklist"
    compileSdkPreview = "UpsideDownCake"

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
detekt {
    buildUponDefaultConfig = false
    allRules = false
    baseline = file("$rootDir/detekt/baseline.xml")
    input = files("src/main/java/com")
    debug = true
    parallel = true
    reportsDir = file("reports_detekt")
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
    implementation(libs.timber)
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
    implementation(libs.bundles.room)
    implementation(libs.bundles.retrofit)
//    kapt(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)
    detektPlugins(libs.detekt.plugin)
    detektPlugins(libs.detekt.formatting)
}