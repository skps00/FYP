plugins {
    id("com.android.application")
}

android {
    namespace = "com.calendar.fyp"
    compileSdk = 34

    buildFeatures {
        viewBinding = true
        dataBinding = true


    }

    defaultConfig {
        applicationId = "com.calendar.fyp"
        minSdk = 29
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packagingOptions {
        exclude("**/META-INF/INDEX.LIST")
        exclude( "**/META-INF/DEPENDENCIES")
    }


}

dependencies {
    implementation("androidx.navigation:navigation-fragment-ktx:2.4.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.4.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.5.0")

    implementation("androidx.core:core:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    // Java V2
    implementation("io.grpc:grpc-okhttp:1.41.0")
    implementation("com.google.cloud:google-cloud-dialogflow:2.2.0")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.1")

    constraints {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.0") {
            because("kotlin-stdlib-jdk7 is now a part of kotlin-stdlib")
        }
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0") {
            because("kotlin-stdlib-jdk8 is now a part of kotlin-stdlib")
        }
    }
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}