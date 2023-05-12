
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
    implementation(libs.legacy.support.v4)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    detektPlugins(libs.detekt.formatting)
    detektPlugins(libs.detekt.plugin)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.bundles.arrow)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.room)
    implementation(libs.dagger)
    implementation(libs.material)
    implementation(libs.sandwich)
    implementation(libs.whatif)
    implementation(libs.timber)
    kapt(libs.dagger.compiler)
    ksp(libs.androidx.room.compiler)
    ksp(libs.arrow.optics.ksp.plugin)
    testImplementation(libs.junit)
    implementation(libs.arrow.optics)
}