plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
}



android {

    namespace = "com.lsepulveda.kotlinudemydelivery"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.lsepulveda.kotlinudemydelivery"
        minSdk = 23
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
    kotlinOptions {
        jvmTarget = "1.8"
    }

}


dependencies {
    // imagen circular
    implementation("de.hdodenhof:circleimageview:3.1.0")
    // ver imagen desde url
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // seleccionar img para elegir de galeria
    implementation("com.github.dhaval2404:imagepicker:2.1")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    //para peticiones http
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // para progress dialog
    implementation("com.github.tommasoberlose:progress-dialog:1.0.0")

    // para deslizar imagenes
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")

    // para google maps
    implementation("com.google.maps.android:maps-ktx:3.2.0")
    implementation("com.google.maps.android:maps-utils-ktx:3.2.0")
    implementation("com.google.android.gms:play-services-maps:17.0.1")
    implementation("com.google.android.gms:play-services-location:18.0.0")
    implementation("com.google.maps.android:android-maps-utils:2.2.3")

    // para view pager
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    // para trazado de rutas
    implementation("com.github.malikdawar:drawroute:1.5")

    // para sockets io
    implementation("com.github.nkzawa:socket.io-client:0.6.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")

    // para firebase
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-messaging-ktx")

    // para text view material
    implementation("com.google.android.material:material:1.12.0")
}