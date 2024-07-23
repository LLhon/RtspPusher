plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.ancda.rtsppusher"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ancda.rtsppusher"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

//        externalNativeBuild {
//            cmake {
//                cppFlags += listOf("")
//            }
//        }

        ndk {
            //设置支持的SO库架构
//            abiFilters += listOf("arm64-v8a", "armeabi-v7a")
            abiFilters += listOf("armeabi-v7a")
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
//        sourceCompatibility = JavaVersion.VERSION_1_8
//        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
//    kotlinOptions {
//        jvmTarget = "1.8"
//    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        //这2个为非必选，想用哪个就保留那个 用的话一定要加上项目中的 ViewBinding & DataBinding 混淆规则
        dataBinding = true
        viewBinding = true
    }

//    externalNativeBuild {
//        cmake {
//            path("CMakeLists.txt")
//            path("src/main/cpp/CMakeLists.txt")
//            version = "3.10.2"
//        }
//    }

    ndkVersion = "21.4.7075529"
}

dependencies {
    add("implementation", fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation(project(":base"))
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.blankj:utilcodex:1.31.0")
    implementation("com.squareup.okhttp3:okhttp:3.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")
    implementation("org.eclipse.ecf:org.apache.commons.codec:1.9.0.v20170208-1614")
    implementation("com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.11")
    implementation("io.github.razerdp:BasePopup:3.2.1")
    implementation("com.github.getActivity:XXPermissions:18.6")
    implementation("com.kingja.loadsir:loadsir:1.3.8")
    implementation("com.github.lygttpod:SuperTextView:2.4.6")
    implementation("com.tencent:mmkv:1.2.16")
    implementation("com.github.ybq:Android-SpinKit:1.2.0")
//    implementation("com.github.onlyloveyd:LazyKeyboard:v1.5")
    implementation("com.elvishew:xlog:1.11.0")
    implementation("com.github.hackware1993:MagicIndicator:1.7.0")
    implementation("com.kyleduo.switchbutton:library:2.1.0")
    implementation("com.github.loper7:DateTimePicker:0.6.3")
    implementation("com.github.gzu-liyujiang.AndroidPicker:WheelPicker:4.1.13")
    implementation("io.github.uni-cstar:oknet:0.0.3")
    implementation("com.qiniu:qiniu-android-sdk:7.2.1")

    implementation("com.tencent.bugly:crashreport:latest.release") //其中latest.release指代最新Bugly SDK版本号，也可以指定明确的版本号，例如4.0.3

    //GSYVideoPlayer
//    implementation("com.github.CarGuo.GSYVideoPlayer:GSYVideoPlayer:v8.6.0-release-jitpack")
    implementation("com.github.CarGuo.GSYVideoPlayer:gsyVideoPlayer-java:v8.6.0-release-jitpack")
    //是否需要ExoPlayer模式
    implementation("com.github.CarGuo.GSYVideoPlayer:GSYVideoPlayer-exo2:v8.6.0-release-jitpack")
    //根据你的需求ijk模式的so
//    implementation("com.github.CarGuo.GSYVideoPlayer:gsyVideoPlayer-arm64:v8.6.0-release-jitpack")
//    implementation("com.github.CarGuo.GSYVideoPlayer:gsyVideoPlayer-armv7a:v8.6.0-release-jitpack")
    //更多ijk的编码支持
    implementation("com.github.CarGuo.GSYVideoPlayer:gsyVideoPlayer-ex_so:v8.6.0-release-jitpack")

    implementation("com.google.android.exoplayer:exoplayer:2.18.7")
    implementation("com.google.android.exoplayer:exoplayer-rtsp:2.19.1")
    // 如果只需要基础服务，可只选择这两个
    implementation("com.google.android.exoplayer:exoplayer-core:2.19.1")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.18.7")

//    implementation("androidx.media3:media3-exoplayer:1.3.1")
//    implementation("androidx.media3:media3-exoplayer-rtsp:1.3.1")
//    implementation("androidx.media3:media3-ui:1.3.1")

    implementation("org.videolan.android:libvlc-all:4.0.0-eap15")
//    implementation("com.arthenica:ffmpeg-kit-min-gpl:6.0-2")
    implementation("com.arthenica:smart-exception-java:0.2.1")
}