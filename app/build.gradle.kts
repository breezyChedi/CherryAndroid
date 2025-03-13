plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.cherry.cherri"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.cherry.cherri"
        minSdk = 24
        targetSdk = 34
        versionCode = 6
        versionName = "1.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


        // Add the properties to BuildConfig
        buildConfigField("String", "NEO4J_URI", "\"${project.findProperty("NEO4J_URI") ?: ""}\"")
        buildConfigField("String", "NEO4J_USERNAME", "\"${project.findProperty("NEO4J_USERNAME") ?: ""}\"")
        buildConfigField("String", "NEO4J_PASSWORD", "\"${project.findProperty("NEO4J_PASSWORD") ?: ""}\"")
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
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.media3.common.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(platform(libs.firebase.bom)) // Firebase Bill of Materials
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.neo4j.driver)
    implementation(libs.androidx.constraintlayout) // ConstraintLayout
    implementation(libs.cardview)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.androidx.lifecycle.runtime.ktx)
// Add CardView

}