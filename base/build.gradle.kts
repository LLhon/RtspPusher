plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.ancda.base"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

}

dependencies {
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("androidx.core:core-ktx:1.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    api("androidx.activity:activity-ktx:1.4.0")
    api("androidx.fragment:fragment-ktx:1.4.0")
    api("com.kunminx.arch:unpeek-livedata:7.8.0")
    //retrofit
//    api "com.squareup.retrofit2:retrofit:2.9.0"
    api("com.squareup.retrofit2:converter-gson:2.9.0")
    api("com.squareup.okhttp3:logging-interceptor:3.10.0")
    api("com.github.franmontiel:PersistentCookieJar:v1.0.1")
    //util
    api("com.blankj:utilcodex:1.31.0")
    //gson容错
    api("com.github.getActivity:GsonFactory:9.5")
    api("com.google.code.gson:gson:2.10.1")
}